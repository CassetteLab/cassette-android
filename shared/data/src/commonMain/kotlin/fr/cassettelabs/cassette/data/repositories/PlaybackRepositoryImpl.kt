package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.data.local.dao.PlaybackQueueDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.embeddeds.PlaybackQueueItemWithTrack
import fr.cassettelabs.cassette.data.local.entities.PlaybackQueueItemEntity
import fr.cassettelabs.cassette.data.local.entities.PlaybackQueueItemSource
import fr.cassettelabs.cassette.data.local.entities.PlaybackQueueSection
import fr.cassettelabs.cassette.data.local.entities.PlaybackSessionEntity
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.data.remote.ktor.md5
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngineListener
import fr.cassettelabs.cassette.data.remote.player.PlayerState
import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackContextType
import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath

internal class PlaybackRepositoryImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val playbackQueueDao: PlaybackQueueDao,
    private val cipherHelper: CipherHelper,
    private val playerEngine: PlayerEngine,
) : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<CurrentTrack?>(null)
    override val currentTrack: StateFlow<CurrentTrack?> = _currentTrack

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var positionUpdatesJob: Job? = null
    private var queueState = QueueState()
    private var restoredPositionMs = 0L

    init {
        playerEngine.setListener(
            object : PlayerEngineListener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlaybackState()
                    if (isPlaying) {
                        startPositionUpdates()
                    } else {
                        stopPositionUpdates()
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    updatePlaybackState()
                    if (state == PlayerState.ENDED) {
                        scope.launch { skipToNext() }
                    }
                }

                override fun onPlayerError(message: String) {
                    stopPositionUpdates()
                    updatePlaybackState()
                }
            },
        )

        scope.launch { restoreQueue() }
    }

    override suspend fun play(
        currentTrack: CurrentTrack,
        contextTracks: List<CurrentTrack>,
        context: PlaybackContext?,
    ) {
        val upcoming = contextTracks.dropWhile { it.track.id != currentTrack.track.id }.drop(1)
        queueState =
            QueueState(
                current = currentTrack,
                contextQueue = upcoming,
                context = context,
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode,
            )
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = currentTrack, positionMs = 0L, shouldPlay = true)
    }

    override fun play() {
        val currentTrack = _currentTrack.value
        if (playerEngine.duration == 0L && playerEngine.currentPosition == 0L && currentTrack != null) {
            scope.launch {
                playTrack(currentTrack = currentTrack, positionMs = restoredPositionMs, shouldPlay = true)
            }
        } else {
            playerEngine.play()
            updatePlaybackState()
        }
    }

    override fun pause() {
        playerEngine.pause()
        updatePlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        playerEngine.seekTo(positionMs)
        restoredPositionMs = positionMs
        scope.launch(Dispatchers.IO) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), currentTimeMillis())
        }
        updatePlaybackState(positionMs = positionMs)
    }

    override suspend fun skipToNext() {
        if (queueState.repeatMode == RepeatMode.One) {
            withContext(Dispatchers.Main.immediate) {
                playerEngine.seekTo(0L)
                playerEngine.play()
                updatePlaybackState(positionMs = 0L)
            }
            return
        }

        val current = queueState.current ?: return
        val next = queueState.userQueue.firstOrNull() ?: queueState.contextQueue.firstOrNull()
        if (next == null) {
            if (queueState.repeatMode == RepeatMode.All) {
                restartQueueFromBeginning()
            } else {
                pause()
            }
            return
        }

        queueState =
            queueState.copy(
                history = queueState.history + current,
                current = next,
                userQueue = queueState.userQueue.drop(1).takeIf { queueState.userQueue.firstOrNull() == next } ?: queueState.userQueue,
                contextQueue = queueState.contextQueue.drop(1).takeIf { queueState.contextQueue.firstOrNull() == next } ?: queueState.contextQueue,
            )
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = next, positionMs = 0L, shouldPlay = true)
    }

    override suspend fun skipToPrevious() {
        if (playerEngine.currentPosition > PREVIOUS_RESTART_THRESHOLD_MS) {
            seekTo(0L)
            return
        }

        val previous = queueState.history.lastOrNull() ?: return
        val current = queueState.current
        queueState =
            queueState.copy(
                history = queueState.history.dropLast(1),
                current = previous,
                contextQueue = listOfNotNull(current) + queueState.contextQueue,
            )
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = previous, positionMs = 0L, shouldPlay = true)
    }

    override suspend fun setShuffleEnabled(isEnabled: Boolean) {
        val contextQueue =
            if (isEnabled) {
                queueState.contextQueue.shuffled()
            } else {
                queueState.contextQueue.sortedWith(compareBy<CurrentTrack> { it.track.trackNumber ?: Int.MAX_VALUE }.thenBy { it.track.title })
            }
        queueState = queueState.copy(isShuffleEnabled = isEnabled, contextQueue = contextQueue)
        persistQueue(positionMs = playerEngine.currentPosition)
    }

    override suspend fun setRepeatMode(repeatMode: RepeatMode) {
        queueState = queueState.copy(repeatMode = repeatMode)
        persistQueue(positionMs = playerEngine.currentPosition)
        updatePlaybackState()
    }

    private suspend fun buildStreamUrl(trackId: String): Pair<String, Map<String, String>> {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration
        val salt = currentTimeMillis().toString(16)
        val password = cipherHelper.decrypt(server.encryptedPassword)
        val url =
            URLBuilder(server.serverUrl.trimEnd('/')).apply {
                encodedPath = "${encodedPath.trimEnd('/')}/rest/stream.view"
                parameters.append("id", trackId)
                parameters.append("u", server.username)
                parameters.append("t", md5(password + salt))
                parameters.append("s", salt)
                parameters.append("v", "1.16.1")
                parameters.append("c", "Cassette")
                parameters.append("format", "mp3")
            }.buildString()
        val headers =
            configuration.customHeaders
                .filter { it.name.isNotBlank() }
                .associate { customHeader -> customHeader.name to cipherHelper.decrypt(customHeader.encryptedValue) }
        return url to headers
    }

    private suspend fun restoreQueue() {
        val session = withContext(Dispatchers.IO) { playbackQueueDao.getSession(ACTIVE_SESSION_ID) } ?: return
        val items = withContext(Dispatchers.IO) { playbackQueueDao.getItemsWithTracks(ACTIVE_SESSION_ID) }
        queueState =
            QueueState(
                history = items.itemsIn(PlaybackQueueSection.History),
                current = items.itemsIn(PlaybackQueueSection.Current).firstOrNull(),
                userQueue = items.itemsIn(PlaybackQueueSection.UserQueue),
                contextQueue = items.itemsIn(PlaybackQueueSection.ContextQueue),
                context = session.context(),
                isShuffleEnabled = session.isShuffleEnabled,
                repeatMode = session.repeatMode(),
            )
        restoredPositionMs = session.currentPositionMs
        _currentTrack.value = queueState.current
        updatePlaybackState(positionMs = session.currentPositionMs)
    }

    private suspend fun restartQueueFromBeginning() {
        val allContextTracks = queueState.history + listOfNotNull(queueState.current) + queueState.contextQueue
        val nextQueue = if (queueState.isShuffleEnabled) allContextTracks.shuffled() else allContextTracks
        val next = nextQueue.firstOrNull() ?: return
        queueState = queueState.copy(history = emptyList(), current = next, userQueue = emptyList(), contextQueue = nextQueue.drop(1))
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = next, positionMs = 0L, shouldPlay = true)
    }

    private suspend fun playTrack(
        currentTrack: CurrentTrack,
        positionMs: Long,
        shouldPlay: Boolean,
    ) {
        val (url, headers) = buildStreamUrl(currentTrack.track.id)
        withContext(Dispatchers.Main.immediate) {
            restoredPositionMs = positionMs
            _currentTrack.value = currentTrack
            playerEngine.setMediaItem(url, headers, positionMs)
            if (shouldPlay) playerEngine.play()
            updatePlaybackState(positionMs = positionMs)
        }
    }

    private suspend fun persistQueue(positionMs: Long) {
        val now = currentTimeMillis()
        val session =
            PlaybackSessionEntity(
                id = ACTIVE_SESSION_ID,
                currentPositionMs = positionMs.coerceAtLeast(0L),
                contextType = queueState.context?.type?.name,
                contextId = queueState.context?.id,
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode.name,
                updatedAt = now,
            )
        val items = queueState.toEntities(now)
        withContext(Dispatchers.IO) { playbackQueueDao.replaceQueue(session = session, items = items) }
        updatePlaybackState(positionMs = positionMs)
    }

    private fun startPositionUpdates() {
        if (positionUpdatesJob?.isActive == true) return

        positionUpdatesJob =
            scope.launch {
                while (isActive) {
                    updatePlaybackState()
                    delay(POSITION_UPDATE_INTERVAL_MS)
                }
            }
    }

    private fun stopPositionUpdates() {
        positionUpdatesJob?.cancel()
        positionUpdatesJob = null
    }

    private fun updatePlaybackState(positionMs: Long = playerEngine.currentPosition) {
        val duration = playerEngine.duration.takeIf { it > 0L } ?: 0L
        _playbackState.value =
            PlaybackState(
                isPlaying = playerEngine.isPlaying,
                positionMs = positionMs.coerceAtLeast(0L),
                durationMs = duration,
                bufferedPositionMs = playerEngine.bufferedPosition.coerceAtLeast(0L),
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode,
            )
        scope.launch(Dispatchers.IO) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), currentTimeMillis())
        }
    }

    private companion object {
        const val ACTIVE_SESSION_ID = "active"
        const val POSITION_UPDATE_INTERVAL_MS = 500L
        const val PREVIOUS_RESTART_THRESHOLD_MS = 5_000L
    }
}

private data class QueueState(
    val history: List<CurrentTrack> = emptyList(),
    val current: CurrentTrack? = null,
    val userQueue: List<CurrentTrack> = emptyList(),
    val contextQueue: List<CurrentTrack> = emptyList(),
    val context: PlaybackContext? = null,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.Off,
)

private fun QueueState.toEntities(addedAt: Long): List<PlaybackQueueItemEntity> =
    history.toEntities(PlaybackQueueSection.History, PlaybackQueueItemSource.Context, addedAt) +
        listOfNotNull(current).toEntities(PlaybackQueueSection.Current, PlaybackQueueItemSource.Context, addedAt) +
        userQueue.toEntities(PlaybackQueueSection.UserQueue, PlaybackQueueItemSource.UserAdded, addedAt) +
        contextQueue.toEntities(PlaybackQueueSection.ContextQueue, PlaybackQueueItemSource.Context, addedAt)

private fun List<CurrentTrack>.toEntities(
    section: PlaybackQueueSection,
    source: PlaybackQueueItemSource,
    addedAt: Long,
): List<PlaybackQueueItemEntity> =
    mapIndexed { index, currentTrack ->
        PlaybackQueueItemEntity(
            id = "${section.name}-$index-${currentTrack.track.id}",
            sessionId = "active",
            section = section.name,
            source = source.name,
            position = index,
            trackId = currentTrack.track.id,
            addedAt = addedAt,
        )
    }

private fun PlaybackSessionEntity.context(): PlaybackContext? {
    val typeName = contextType ?: return null
    val id = contextId ?: return null
    val type =
        try {
            PlaybackContextType.valueOf(typeName)
        } catch (_: IllegalArgumentException) {
            return null
        }
    return PlaybackContext(type = type, id = id)
}

private fun PlaybackSessionEntity.repeatMode(): RepeatMode =
    try {
        RepeatMode.valueOf(repeatMode)
    } catch (_: IllegalArgumentException) {
        RepeatMode.Off
    }

private fun List<PlaybackQueueItemWithTrack>.itemsIn(section: PlaybackQueueSection): List<CurrentTrack> =
    filter { it.section == section.name }
        .sortedBy { it.position }
        .map { it.toDomain() }

private fun PlaybackQueueItemWithTrack.toDomain(): CurrentTrack =
    CurrentTrack(
        track =
            Track(
                id = trackId,
                title = title,
                artist = artist,
                trackNumber = trackNumber,
                durationSeconds = durationSeconds,
            ),
        albumId = albumId,
        albumName = albumName,
        coverArtId = coverArtId,
        coverArtFilePath = coverArtFilePath,
    )

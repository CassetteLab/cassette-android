package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.core.coroutines.CoroutineDispatchers
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
import fr.cassettelabs.cassette.data.remote.player.PlatformMediaSessionCallbacks
import fr.cassettelabs.cassette.data.remote.player.PlatformMediaSessionController
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackContextType
import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
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
    private val mediaSessionController: PlatformMediaSessionController,
    private val albumRepository: AlbumRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
) : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _playbackQueue = MutableStateFlow<List<Track>>(emptyList())
    override val playbackQueue: StateFlow<List<Track>> = _playbackQueue

    private val scope = CoroutineScope(SupervisorJob() + coroutineDispatchers.mainImmediate)
    private var positionUpdatesJob: Job? = null
    private var queueState = QueueState()
    private var restoredPositionMs = 0L

    init {
        mediaSessionController.setCallbacks(
            PlatformMediaSessionCallbacks(
                play = ::play,
                pause = ::pause,
                seekTo = ::seekTo,
                skipToNext = { scope.launch { skipToNext() } },
                skipToPrevious = { scope.launch { skipToPrevious() } },
            ),
        )

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
        currentTrack: Track,
        contextTracks: List<Track>,
        context: PlaybackContext?,
    ) {
        val upcoming = contextTracks.dropWhile { it.id != currentTrack.id }.drop(1)
        queueState =
            QueueState(
                current = currentTrack,
                contextQueue = upcoming,
                context = context,
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode,
            )
        updatePlaybackQueue()
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
        scope.launch(coroutineDispatchers.io) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), currentTimeMillis())
        }
        updatePlaybackState(positionMs = positionMs)
    }

    override suspend fun skipToNext() {
        if (queueState.repeatMode == RepeatMode.One) {
            withContext(coroutineDispatchers.mainImmediate) {
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
        updatePlaybackQueue()
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
        updatePlaybackQueue()
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = previous, positionMs = 0L, shouldPlay = true)
    }

    override suspend fun setShuffleEnabled(isEnabled: Boolean) {
        val contextQueue =
            if (isEnabled) {
                queueState.contextQueue.shuffled()
            } else {
                queueState.contextQueue.sortedWith(compareBy<Track> { it.trackNumber ?: Int.MAX_VALUE }.thenBy { it.title })
            }
        queueState = queueState.copy(isShuffleEnabled = isEnabled, contextQueue = contextQueue)
        updatePlaybackQueue()
        persistQueue(positionMs = playerEngine.currentPosition)
    }

    override suspend fun setRepeatMode(repeatMode: RepeatMode) {
        queueState = queueState.copy(repeatMode = repeatMode)
        persistQueue(positionMs = playerEngine.currentPosition)
        updatePlaybackState()
    }

    override suspend fun reorderPlaybackQueue(
        fromIndex: Int,
        toIndex: Int,
    ) {
        val upcomingQueue = (queueState.userQueue + queueState.contextQueue).move(fromIndex = fromIndex, toIndex = toIndex) ?: return
        queueState = queueState.copy(userQueue = emptyList(), contextQueue = upcomingQueue)
        updatePlaybackQueue()
        persistQueue(positionMs = playerEngine.currentPosition)
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
        val session = withContext(coroutineDispatchers.io) { playbackQueueDao.getSession(ACTIVE_SESSION_ID) } ?: return
        val items = withContext(coroutineDispatchers.io) { playbackQueueDao.getItemsWithTracks(ACTIVE_SESSION_ID) }
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
        updatePlaybackQueue()
        updatePlaybackState(positionMs = session.currentPositionMs)
    }

    private suspend fun restartQueueFromBeginning() {
        val allContextTracks = queueState.history + listOfNotNull(queueState.current) + queueState.contextQueue
        val nextQueue = if (queueState.isShuffleEnabled) allContextTracks.shuffled() else allContextTracks
        val next = nextQueue.firstOrNull() ?: return
        queueState = queueState.copy(history = emptyList(), current = next, userQueue = emptyList(), contextQueue = nextQueue.drop(1))
        updatePlaybackQueue()
        persistQueue(positionMs = 0L)
        playTrack(currentTrack = next, positionMs = 0L, shouldPlay = true)
    }

    private suspend fun playTrack(
        currentTrack: Track,
        positionMs: Long,
        shouldPlay: Boolean,
    ) {
        val (url, headers) = buildStreamUrl(currentTrack.id)
        withContext(coroutineDispatchers.mainImmediate) {
            restoredPositionMs = positionMs
            _currentTrack.value = currentTrack
            playerEngine.setMediaItem(url, headers, positionMs)
            if (shouldPlay) playerEngine.play()
            updatePlaybackState(positionMs = positionMs)
            loadCurrentTrackCoverArtIfNeeded(currentTrack)
        }
    }

    private fun loadCurrentTrackCoverArtIfNeeded(currentTrack: Track) {
        if (currentTrack.coverArtFilePath != null) return

        val coverArtId = currentTrack.coverArt ?: return
        val trackId = currentTrack.id
        scope.launch {
            runCatching {
                albumRepository
                    .getAlbumCoverArt(coverArtId = coverArtId, size = COVER_ART_SIZE, albumId = currentTrack.albumId)
                    .filterIsInstance<CoverArtLoadingStatus.Loaded>()
                    .first()
            }.onSuccess { status ->
                val activeTrack = _currentTrack.value ?: return@onSuccess
                if (activeTrack.id != trackId) return@onSuccess

                _currentTrack.value = activeTrack.copy(coverArtFilePath = status.filePath)
                mediaSessionController.update(_currentTrack.value, _playbackState.value)
            }
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
        withContext(coroutineDispatchers.io) { playbackQueueDao.replaceQueue(session = session, items = items) }
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
        mediaSessionController.update(_currentTrack.value, _playbackState.value)
        scope.launch(coroutineDispatchers.io) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), currentTimeMillis())
        }
    }

    private fun updatePlaybackQueue() {
        _playbackQueue.value = queueState.userQueue + queueState.contextQueue
    }

    private companion object {
        const val ACTIVE_SESSION_ID = "active"
        const val POSITION_UPDATE_INTERVAL_MS = 500L
        const val PREVIOUS_RESTART_THRESHOLD_MS = 5_000L
        const val COVER_ART_SIZE = 900
    }
}

private data class QueueState(
    val history: List<Track> = emptyList(),
    val current: Track? = null,
    val userQueue: List<Track> = emptyList(),
    val contextQueue: List<Track> = emptyList(),
    val context: PlaybackContext? = null,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.Off,
)

private fun QueueState.toEntities(addedAt: Long): List<PlaybackQueueItemEntity> =
    history.toEntities(PlaybackQueueSection.History, PlaybackQueueItemSource.Context, addedAt) +
        listOfNotNull(current).toEntities(PlaybackQueueSection.Current, PlaybackQueueItemSource.Context, addedAt) +
        userQueue.toEntities(PlaybackQueueSection.UserQueue, PlaybackQueueItemSource.UserAdded, addedAt) +
        contextQueue.toEntities(PlaybackQueueSection.ContextQueue, PlaybackQueueItemSource.Context, addedAt)

private fun List<Track>.toEntities(
    section: PlaybackQueueSection,
    source: PlaybackQueueItemSource,
    addedAt: Long,
): List<PlaybackQueueItemEntity> =
    mapIndexed { index, track ->
        PlaybackQueueItemEntity(
            id = "${section.name}-$index-${track.id}",
            sessionId = "active",
            section = section.name,
            source = source.name,
            position = index,
            trackId = track.id,
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

private fun List<PlaybackQueueItemWithTrack>.itemsIn(section: PlaybackQueueSection): List<Track> =
    filter { it.section == section.name }
        .sortedBy { it.position }
        .map { it.toDomain() }

private fun List<Track>.move(
    fromIndex: Int,
    toIndex: Int,
): List<Track>? {
    if (fromIndex !in indices || toIndex !in indices || fromIndex == toIndex) return null

    return toMutableList().apply {
        add(toIndex, removeAt(fromIndex))
    }
}

private fun PlaybackQueueItemWithTrack.toDomain(): Track =
    Track(
        albumId = albumId,
        id = trackId,
        title = title,
        artist = artist,
        trackNumber = trackNumber,
        durationSeconds = durationSeconds,
        albumName = albumName,
        coverArt = coverArtId,
        coverArtFilePath = coverArtFilePath,
    )

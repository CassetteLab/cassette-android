package fr.cassette.cassette.data.repositories

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.data.local.dao.PlaybackQueueDao
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.embeddeds.PlaybackQueueItemWithTrack
import fr.cassette.cassette.data.local.entities.PlaybackQueueItemEntity
import fr.cassette.cassette.data.local.entities.PlaybackQueueItemSource
import fr.cassette.cassette.data.local.entities.PlaybackQueueSection
import fr.cassette.cassette.data.local.entities.PlaybackSessionEntity
import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackContext
import fr.cassette.cassette.domain.models.PlaybackState
import fr.cassette.cassette.domain.models.RepeatMode
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

@OptIn(UnstableApi::class)
internal class PlaybackRepositoryImpl(
    context: Context,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val playbackQueueDao: PlaybackQueueDao,
    private val cipherHelper: CipherHelper,
) : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<CurrentTrack?>(null)
    override val currentTrack: StateFlow<CurrentTrack?> = _currentTrack

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    private val player =
        ExoPlayer
            .Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(httpDataSourceFactory))
            .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var positionUpdatesJob: Job? = null
    private var queueState = QueueState()
    private var restoredPositionMs = 0L

    init {
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlaybackState()
                    if (isPlaying) {
                        startPositionUpdates()
                    } else {
                        stopPositionUpdates()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updatePlaybackState()
                    if (playbackState == Player.STATE_ENDED) {
                        scope.launch { skipToNext() }
                    }
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
        if (player.mediaItemCount == 0 && currentTrack != null) {
            scope.launch {
                playTrack(currentTrack = currentTrack, positionMs = restoredPositionMs, shouldPlay = true)
            }
        } else {
            player.play()
            updatePlaybackState()
        }
    }

    override fun pause() {
        player.pause()
        updatePlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        restoredPositionMs = positionMs
        scope.launch(Dispatchers.IO) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), System.currentTimeMillis())
        }
        updatePlaybackState(positionMs = positionMs)
    }

    override suspend fun skipToNext() {
        if (queueState.repeatMode == RepeatMode.One) {
            withContext(Dispatchers.Main.immediate) {
                player.seekTo(0L)
                player.play()
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
        if (player.currentPosition > PREVIOUS_RESTART_THRESHOLD_MS) {
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
        persistQueue(positionMs = player.currentPosition)
    }

    override suspend fun setRepeatMode(repeatMode: RepeatMode) {
        queueState = queueState.copy(repeatMode = repeatMode)
        persistQueue(positionMs = player.currentPosition)
        updatePlaybackState()
    }

    private suspend fun buildStreamRequest(trackId: String): StreamRequest {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration
        val salt = System.currentTimeMillis().toString(16)
        val password = cipherHelper.decrypt(server.encryptedPassword)
        val uri =
            Uri
                .parse("${server.serverUrl.trimEnd('/')}/rest/stream.view")
                .buildUpon()
                .appendQueryParameter("id", trackId)
                .appendQueryParameter("u", server.username)
                .appendQueryParameter("t", md5(password + salt))
                .appendQueryParameter("s", salt)
                .appendQueryParameter("v", "1.16.1")
                .appendQueryParameter("c", "Cassette")
                .build()
        val headers =
            configuration.customHeaders
                .filter { it.name.isNotBlank() }
                .associate { customHeader -> customHeader.name to cipherHelper.decrypt(customHeader.encryptedValue) }
        return StreamRequest(uri = uri, headers = headers)
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
                repeatMode = session.repeatMode,
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
        val streamRequest = buildStreamRequest(currentTrack.track.id)
        withContext(Dispatchers.Main.immediate) {
            restoredPositionMs = positionMs
            _currentTrack.value = currentTrack
            httpDataSourceFactory.setDefaultRequestProperties(streamRequest.headers)
            player.setMediaItem(MediaItem.fromUri(streamRequest.uri))
            player.prepare()
            if (positionMs > 0L) player.seekTo(positionMs)
            if (shouldPlay) player.play()
            updatePlaybackState(positionMs = positionMs)
        }
    }

    private suspend fun persistQueue(positionMs: Long) {
        val now = System.currentTimeMillis()
        val session =
            PlaybackSessionEntity(
                id = ACTIVE_SESSION_ID,
                currentPositionMs = positionMs.coerceAtLeast(0L),
                contextType = queueState.context?.type,
                contextId = queueState.context?.id,
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode,
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

    private fun updatePlaybackState(positionMs: Long = player.currentPosition) {
        _playbackState.value =
            PlaybackState(
                isPlaying = player.isPlaying,
                positionMs = positionMs.coerceAtLeast(0L),
                durationMs = player.duration.takeIf { it != C.TIME_UNSET }?.coerceAtLeast(0L) ?: 0L,
                bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
                isShuffleEnabled = queueState.isShuffleEnabled,
                repeatMode = queueState.repeatMode,
            )
        scope.launch(Dispatchers.IO) {
            playbackQueueDao.updateCurrentPosition(ACTIVE_SESSION_ID, positionMs.coerceAtLeast(0L), System.currentTimeMillis())
        }
    }

    private fun md5(value: String): String =
        MessageDigest
            .getInstance("MD5")
            .digest(value.toByteArray())
            .joinToString(separator = "") { byte -> "%02x".format(byte) }

    private data class StreamRequest(
        val uri: Uri,
        val headers: Map<String, String>,
    )

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
            section = section,
            source = source,
            position = index,
            trackId = currentTrack.track.id,
            addedAt = addedAt,
        )
    }

private fun PlaybackSessionEntity.context(): PlaybackContext? {
    val type = contextType ?: return null
    val id = contextId ?: return null
    return PlaybackContext(type = type, id = id)
}

private fun List<PlaybackQueueItemWithTrack>.itemsIn(section: PlaybackQueueSection): List<CurrentTrack> =
    filter { it.section == section }
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

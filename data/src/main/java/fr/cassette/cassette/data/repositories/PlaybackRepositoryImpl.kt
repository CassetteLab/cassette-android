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
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackState
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
    private val cipherHelper: CipherHelper,
) : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<CurrentTrack?>(null)
    override val currentTrack: StateFlow<CurrentTrack?> = _currentTrack

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    private val player = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(httpDataSourceFactory))
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var positionUpdatesJob: Job? = null

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
                }
            },
        )
    }

    override suspend fun play(currentTrack: CurrentTrack) {
        val streamRequest = buildStreamRequest(currentTrack.track.id)
        withContext(Dispatchers.Main.immediate) {
            _currentTrack.value = currentTrack
            httpDataSourceFactory.setDefaultRequestProperties(streamRequest.headers)
            player.setMediaItem(MediaItem.fromUri(streamRequest.uri))
            player.prepare()
            player.play()
            updatePlaybackState()
        }
    }

    override fun play() {
        player.play()
        updatePlaybackState()
    }

    override fun pause() {
        player.pause()
        updatePlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        updatePlaybackState(positionMs = positionMs)
    }

    private suspend fun buildStreamRequest(trackId: String): StreamRequest {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration
        val salt = System.currentTimeMillis().toString(16)
        val password = cipherHelper.decrypt(server.encryptedPassword)
        val uri = Uri.parse("${server.serverUrl.trimEnd('/')}/rest/stream.view")
            .buildUpon()
            .appendQueryParameter("id", trackId)
            .appendQueryParameter("u", server.username)
            .appendQueryParameter("t", md5(password + salt))
            .appendQueryParameter("s", salt)
            .appendQueryParameter("v", "1.16.1")
            .appendQueryParameter("c", "Cassette")
            .build()
        val headers = configuration.customHeaders
            .filter { it.name.isNotBlank() }
            .associate { customHeader -> customHeader.name to cipherHelper.decrypt(customHeader.encryptedValue) }
        return StreamRequest(uri = uri, headers = headers)
    }

    private fun startPositionUpdates() {
        if (positionUpdatesJob?.isActive == true) return

        positionUpdatesJob = scope.launch {
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
        _playbackState.value = PlaybackState(
            isPlaying = player.isPlaying,
            positionMs = positionMs.coerceAtLeast(0L),
            durationMs = player.duration.takeIf { it != C.TIME_UNSET }?.coerceAtLeast(0L) ?: 0L,
            bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
        )
    }

    private fun md5(value: String): String = MessageDigest.getInstance("MD5")
        .digest(value.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }

    private data class StreamRequest(
        val uri: Uri,
        val headers: Map<String, String>,
    )

    private companion object {
        const val POSITION_UPDATE_INTERVAL_MS = 500L
    }
}

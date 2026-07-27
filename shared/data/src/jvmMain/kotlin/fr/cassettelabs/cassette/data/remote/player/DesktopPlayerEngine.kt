package fr.cassettelabs.cassette.data.remote.player

import javazoom.jl.player.advanced.AdvancedPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DesktopPlayerEngine : PlayerEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
    private val playbackLock = Any()
    private var listener: PlayerEngineListener? = null
    private var preparedUrl: String? = null
    private var preparedHeaders: Map<String, String> = emptyMap()
    private var playbackJob: Job? = null
    private var positionJob: Job? = null
    private var player: AdvancedPlayer? = null
    private var _currentPosition: Long = 0L
    private var _duration: Long = 0L
    private var _bufferedPosition: Long = 0L
    private var _isPlaying: Boolean = false
    private var startedAtMs: Long = 0L
    private var basePositionMs: Long = 0L
    private var playbackGeneration: Long = 0L

    override val currentPosition: Long get() = _currentPosition
    override val duration: Long get() = _duration
    override val bufferedPosition: Long get() = _bufferedPosition
    override val isPlaying: Boolean get() = _isPlaying

    override fun setListener(listener: PlayerEngineListener?) {
        this.listener = listener
    }

    override suspend fun setMediaItem(
        url: String,
        headers: Map<String, String>,
        positionMs: Long,
    ) {
        stopPlayback()
        preparedUrl = url
        preparedHeaders = headers
        _currentPosition = positionMs
        basePositionMs = positionMs
        _duration = 0L
        _bufferedPosition = _duration
        listener?.onPlaybackStateChanged(PlayerState.READY)
    }

    override fun play() {
        val url = preparedUrl ?: return
        val headers = preparedHeaders
        val startPositionMs = basePositionMs
        val generation =
            synchronized(playbackLock) {
                if (_isPlaying) return
                playbackGeneration += 1
                _isPlaying = true
                playbackGeneration
            }

        playbackJob =
            scope.launch {
                try {
                    startedAtMs = System.currentTimeMillis()
                    listener?.onIsPlayingChanged(true)
                    listener?.onPlaybackStateChanged(PlayerState.PLAYING)
                    startPositionUpdates()

                    openStream(url, headers).use { stream ->
                        if (!isCurrentGeneration(generation)) return@use

                        val activePlayer = AdvancedPlayer(stream)
                        synchronized(playbackLock) {
                            if (isCurrentGeneration(generation)) {
                                player = activePlayer
                            } else {
                                activePlayer.close()
                                return@use
                            }
                        }

                        val startFrame = frameForPosition(startPositionMs)
                        if (startFrame == 0) {
                            activePlayer.play()
                        } else {
                            activePlayer.play(startFrame, Int.MAX_VALUE)
                        }
                    }

                    if (isCurrentGeneration(generation)) {
                        _isPlaying = false
                        listener?.onIsPlayingChanged(false)
                        listener?.onPlaybackStateChanged(PlayerState.ENDED)
                    }
                } catch (exception: Exception) {
                    if (isCurrentGeneration(generation)) {
                        listener?.onPlayerError(exception.message ?: "Unable to play media")
                        listener?.onPlaybackStateChanged(PlayerState.ERROR)
                        _isPlaying = false
                        listener?.onIsPlayingChanged(false)
                    }
                } finally {
                    if (isCurrentGeneration(generation)) {
                        stopPositionUpdates()
                        player = null
                    }
                }
            }
    }

    override fun pause() {
        basePositionMs = _currentPosition
        stopPlayback()
        listener?.onPlaybackStateChanged(PlayerState.PAUSED)
    }

    override fun seekTo(positionMs: Long) {
        basePositionMs = positionMs.coerceAtLeast(0L)
        _currentPosition = basePositionMs
        if (_isPlaying) {
            stopPlayback()
            play()
        }
    }

    override fun dispose() {
        preparedUrl = null
        stopPlayback()
    }

    private fun stopPlayback() {
        synchronized(playbackLock) {
            playbackGeneration += 1
            _isPlaying = false
            player?.close()
            player = null
            playbackJob?.cancel()
            playbackJob = null
        }
        stopPositionUpdates()
        listener?.onIsPlayingChanged(false)
    }

    private fun isCurrentGeneration(generation: Long): Boolean = synchronized(playbackLock) { playbackGeneration == generation }

    private fun startPositionUpdates() {
        if (positionJob?.isActive == true) return
        positionJob =
            scope.launch {
                while (isActive && _isPlaying) {
                    _currentPosition = basePositionMs + (System.currentTimeMillis() - startedAtMs)
                    delay(500L)
                }
            }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }

    private fun frameForPosition(positionMs: Long): Int = (positionMs / MP3_FRAME_DURATION_MS).toInt().coerceAtLeast(0)

    private fun openStream(
        url: String,
        headers: Map<String, String>,
    ): BufferedInputStream {
        val builder = HttpRequest.newBuilder().uri(URI.create(url)).GET()
        headers.forEach { (key, value) -> builder.header(key, value) }
        val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream())
        if (response.statusCode() !in 200..299) {
            throw IllegalStateException("Media request failed with HTTP ${response.statusCode()}")
        }
        return BufferedInputStream(response.body())
    }

    private companion object {
        const val MP3_FRAME_DURATION_MS = 26L
    }
}

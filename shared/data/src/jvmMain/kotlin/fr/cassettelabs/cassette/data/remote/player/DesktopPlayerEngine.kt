package fr.cassettelabs.cassette.data.remote.player

import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.io.ByteArrayInputStream
import java.io.File
import java.io.RandomAccessFile
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent

class DesktopPlayerEngine : PlayerEngine {
    private var clip: Clip? = null
    private var listener: PlayerEngineListener? = null
    private var _currentPosition: Long = 0L
    private var _duration: Long = 0L
    private var _bufferedPosition: Long = 0L
    private var _isPlaying: Boolean = false
    private var audioData: ByteArray? = null
    private var preparedUrl: String? = null
    private var isDisposed = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var playbackJob: Job? = null

    override val currentPosition: Long
        get() = _currentPosition
    override val duration: Long
        get() = _duration
    override val bufferedPosition: Long
        get() = _bufferedPosition
    override val isPlaying: Boolean
        get() = _isPlaying

    override fun setListener(listener: PlayerEngineListener?) {
        this.listener = listener
    }

    override suspend fun setMediaItem(
        url: String,
        headers: Map<String, String>,
        positionMs: Long,
    ) {
        stopCurrentPlayback()
        isDisposed = false
        preparedUrl = url

        val builder = HttpRequest.newBuilder().uri(URI.create(url)).GET()
        headers.forEach { (key, value) -> builder.header(key, value) }
        val request = builder.build()
        val client = HttpClient.newHttpClient()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())

        audioData = response.body()
        _duration = 0L
        _currentPosition = positionMs
        _bufferedPosition = audioData?.size?.toLong() ?: 0L

        try {
            val inputStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(audioData))
            val format = inputStream.format
            val info = DataLine.Info(Clip::class.java, format)
            clip = AudioSystem.getLine(info) as? Clip
            clip?.open(inputStream)
            _duration = clip?.microsecondLength?.div(1000) ?: 0L
            if (positionMs > 0L) {
                clip?.microsecondPosition = positionMs * 1000
            }
            clip?.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP && !_isPlaying) {
                    listener?.onPlaybackStateChanged(PlayerState.ENDED)
                }
            }
        } catch (_: Exception) {
            clip = null
        }
    }

    override fun play() {
        clip?.let { c ->
            c.start()
            _isPlaying = true
            listener?.onIsPlayingChanged(true)
            listener?.onPlaybackStateChanged(PlayerState.PLAYING)
            startPositionUpdates()
        }
    }

    override fun pause() {
        clip?.let { c ->
            c.stop()
            _isPlaying = false
            listener?.onIsPlayingChanged(false)
            listener?.onPlaybackStateChanged(PlayerState.PAUSED)
        }
        stopPositionUpdates()
    }

    override fun seekTo(positionMs: Long) {
        clip?.let { c ->
            _currentPosition = positionMs
            c.microsecondPosition = positionMs * 1000
        }
    }

    override fun dispose() {
        isDisposed = true
        stopCurrentPlayback()
        stopPositionUpdates()
    }

    private fun stopCurrentPlayback() {
        clip?.stop()
        clip?.close()
        clip = null
        audioData = null
        _isPlaying = false
    }

    private fun startPositionUpdates() {
        if (playbackJob?.isActive == true) return
        playbackJob =
            scope.launch {
                while (isActive && !isDisposed && _isPlaying) {
                    clip?.let { c ->
                        if (c.isRunning) {
                            _currentPosition = c.microsecondPosition / 1000
                        }
                    }
                    delay(500L)
                }
            }
    }

    private fun stopPositionUpdates() {
        playbackJob?.cancel()
        playbackJob = null
    }
}

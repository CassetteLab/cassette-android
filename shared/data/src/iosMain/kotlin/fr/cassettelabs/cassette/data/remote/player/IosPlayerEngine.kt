package fr.cassettelabs.cassette.data.remote.player

class IosPlayerEngine : PlayerEngine {
    private var listener: PlayerEngineListener? = null
    private var _currentPosition: Long = 0L
    private var _duration: Long = 0L
    private var _isPlaying: Boolean = false

    override val currentPosition: Long get() = _currentPosition
    override val duration: Long get() = _duration
    override val bufferedPosition: Long get() = 0L
    override val isPlaying: Boolean get() = _isPlaying

    override fun setListener(listener: PlayerEngineListener?) {
        this.listener = listener
    }

    override suspend fun setMediaItem(
        url: String,
        headers: Map<String, String>,
        positionMs: Long,
    ) {
        _currentPosition = positionMs
        _duration = 0L
        _isPlaying = false
    }

    override fun play() {
        _isPlaying = true
        listener?.onIsPlayingChanged(true)
    }

    override fun pause() {
        _isPlaying = false
        listener?.onIsPlayingChanged(false)
    }

    override fun seekTo(positionMs: Long) {
        _currentPosition = positionMs
    }

    override fun dispose() {
        pause()
    }
}

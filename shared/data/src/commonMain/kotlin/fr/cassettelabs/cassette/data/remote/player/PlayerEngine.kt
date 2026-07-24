package fr.cassettelabs.cassette.data.remote.player

interface PlayerEngine {
    val currentPosition: Long
    val duration: Long
    val bufferedPosition: Long
    val isPlaying: Boolean

    fun setListener(listener: PlayerEngineListener?)
    suspend fun setMediaItem(url: String, headers: Map<String, String>, positionMs: Long)
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
    fun dispose()
}

interface PlayerEngineListener {
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun onPlaybackStateChanged(state: Int)
}

object PlayerState {
    const val IDLE = 0
    const val READY = 1
    const val PLAYING = 2
    const val PAUSED = 3
    const val ENDED = 4
    const val ERROR = 5
}

package fr.cassettelabs.cassette.data.remote.player

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IosPlayerEngine : PlayerEngine {
    private var listener: PlayerEngineListener? = null
    private var player: AVPlayer? = null

    override val currentPosition: Long
        get() = player?.currentTime()?.toMillis() ?: 0L
    override val duration: Long
        get() = player?.currentItem()?.duration()?.toMillis() ?: 0L
    override val bufferedPosition: Long get() = 0L
    override val isPlaying: Boolean get() = player?.rate()?.let { it > 0.0f } ?: false

    override fun setListener(listener: PlayerEngineListener?) {
        this.listener = listener
    }

    override suspend fun setMediaItem(
        url: String,
        headers: Map<String, String>,
        positionMs: Long,
    ) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl == null) {
            listener?.onPlayerError("Invalid media URL")
            listener?.onPlaybackStateChanged(PlayerState.ERROR)
            return
        }

        val options: Map<Any?, *>? = headers.takeIf { it.isNotEmpty() }?.let { mapOf<Any?, Any>(HTTP_HEADER_FIELDS_OPTION to it) }
        val asset = AVURLAsset.URLAssetWithURL(nsUrl, options)
        val item = AVPlayerItem.playerItemWithAsset(asset)
        player?.pause()
        player = AVPlayer.playerWithPlayerItem(item)
        if (positionMs > 0L) seekTo(positionMs)
        listener?.onPlaybackStateChanged(PlayerState.READY)
    }

    override fun play() {
        player?.play()
        listener?.onIsPlayingChanged(true)
        listener?.onPlaybackStateChanged(PlayerState.PLAYING)
    }

    override fun pause() {
        player?.pause()
        listener?.onIsPlayingChanged(false)
        listener?.onPlaybackStateChanged(PlayerState.PAUSED)
    }

    override fun seekTo(positionMs: Long) {
        player?.seekToTime(CMTimeMake(value = positionMs, timescale = 1000))
    }

    override fun dispose() {
        pause()
        player = null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun kotlinx.cinterop.CValue<platform.CoreMedia.CMTime>.toMillis(): Long {
    val seconds = CMTimeGetSeconds(this)
    return if (seconds.isFinite() && seconds > 0.0) (seconds * 1_000).toLong() else 0L
}

private const val HTTP_HEADER_FIELDS_OPTION = "AVURLAssetHTTPHeaderFieldsKey"

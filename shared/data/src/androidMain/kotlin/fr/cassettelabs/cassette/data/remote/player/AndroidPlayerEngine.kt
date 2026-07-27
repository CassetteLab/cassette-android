package fr.cassettelabs.cassette.data.remote.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@OptIn(UnstableApi::class)
class AndroidPlayerEngine(context: Context) : PlayerEngine {
    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    private val player =
        ExoPlayer
            .Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(httpDataSourceFactory))
            .build()

    private var listener: PlayerEngineListener? = null

    private val playerListener =
        object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                listener?.onIsPlayingChanged(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> listener?.onPlaybackStateChanged(PlayerState.ENDED)
                    Player.STATE_READY -> {
                        if (player.isPlaying) {
                            listener?.onPlaybackStateChanged(PlayerState.PLAYING)
                        } else {
                            listener?.onPlaybackStateChanged(PlayerState.PAUSED)
                        }
                    }
                    Player.STATE_BUFFERING -> listener?.onPlaybackStateChanged(PlayerState.READY)
                    Player.STATE_IDLE -> listener?.onPlaybackStateChanged(PlayerState.IDLE)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Unable to play media", error)
                listener?.onPlayerError(error.message ?: "Unable to play media")
                listener?.onPlaybackStateChanged(PlayerState.ERROR)
            }
        }

    init {
        player.addListener(playerListener)
    }

    override val currentPosition: Long
        get() = player.currentPosition
    override val duration: Long
        get() = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
    override val bufferedPosition: Long
        get() = player.bufferedPosition
    override val isPlaying: Boolean
        get() = player.isPlaying

    override fun setListener(listener: PlayerEngineListener?) {
        this.listener = listener
    }

    override suspend fun setMediaItem(
        url: String,
        headers: Map<String, String>,
        positionMs: Long,
    ) {
        httpDataSourceFactory.setDefaultRequestProperties(headers)
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        if (positionMs > 0L) player.seekTo(positionMs)
    }

    override fun play() = player.play()

    override fun pause() = player.pause()

    override fun seekTo(positionMs: Long) = player.seekTo(positionMs)

    override fun dispose() = player.release()

    private companion object {
        const val TAG = "AndroidPlayerEngine"
    }
}

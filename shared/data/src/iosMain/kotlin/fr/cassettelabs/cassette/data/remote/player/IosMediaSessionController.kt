package fr.cassettelabs.cassette.data.remote.player

import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.MediaPlayer.MPMediaItemPropertyAlbumTitle
import platform.MediaPlayer.MPMediaItemPropertyArtwork
import platform.MediaPlayer.MPMediaItemPropertyArtist
import platform.MediaPlayer.MPMediaItemPropertyPlaybackDuration
import platform.MediaPlayer.MPMediaItemPropertyTitle
import platform.MediaPlayer.MPMediaItemArtwork
import platform.MediaPlayer.MPNowPlayingInfoCenter
import platform.MediaPlayer.MPNowPlayingInfoPropertyElapsedPlaybackTime
import platform.MediaPlayer.MPNowPlayingInfoPropertyPlaybackRate
import platform.MediaPlayer.MPChangePlaybackPositionCommandEvent
import platform.MediaPlayer.MPRemoteCommandCenter
import platform.MediaPlayer.MPRemoteCommandHandlerStatusSuccess
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
class IosMediaSessionController : PlatformMediaSessionController {
    private var callbacks: PlatformMediaSessionCallbacks? = null
    private val commandCenter = MPRemoteCommandCenter.sharedCommandCenter()

    init {
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, error = null)

        commandCenter.playCommand.enabled = true
        commandCenter.pauseCommand.enabled = true
        commandCenter.nextTrackCommand.enabled = true
        commandCenter.previousTrackCommand.enabled = true
        commandCenter.changePlaybackPositionCommand.enabled = true

        commandCenter.playCommand.addTargetWithHandler {
            callbacks?.play()
            MPRemoteCommandHandlerStatusSuccess
        }
        commandCenter.pauseCommand.addTargetWithHandler {
            callbacks?.pause()
            MPRemoteCommandHandlerStatusSuccess
        }
        commandCenter.nextTrackCommand.addTargetWithHandler {
            callbacks?.skipToNext()
            MPRemoteCommandHandlerStatusSuccess
        }
        commandCenter.previousTrackCommand.addTargetWithHandler {
            callbacks?.skipToPrevious()
            MPRemoteCommandHandlerStatusSuccess
        }
        commandCenter.changePlaybackPositionCommand.addTargetWithHandler { event ->
            val positionEvent = event as MPChangePlaybackPositionCommandEvent
            callbacks?.seekTo((positionEvent.positionTime * 1_000.0).toLong())
            MPRemoteCommandHandlerStatusSuccess
        }
    }

    override fun setCallbacks(callbacks: PlatformMediaSessionCallbacks) {
        this.callbacks = callbacks
    }

    override fun update(
        track: Track?,
        playbackState: PlaybackState,
    ) {
        if (track == null) {
            MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = null
            return
        }

        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo =
            buildMap<Any?, Any> {
                put(MPMediaItemPropertyTitle, track.title)
                track.artist?.let { put(MPMediaItemPropertyArtist, it) }
                track.albumName?.let { put(MPMediaItemPropertyAlbumTitle, it) }
                track.coverArtFilePath?.toMediaItemArtwork()?.let { put(MPMediaItemPropertyArtwork, it) }
                put(MPMediaItemPropertyPlaybackDuration, (track.durationSeconds ?: playbackState.durationMs / 1_000L).toDouble())
                put(MPNowPlayingInfoPropertyElapsedPlaybackTime, playbackState.positionMs / 1_000.0)
                put(MPNowPlayingInfoPropertyPlaybackRate, if (playbackState.isPlaying) 1.0 else 0.0)
            }
    }

    override fun dispose() {
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = null
    }

    private fun String.toMediaItemArtwork(): MPMediaItemArtwork? {
        val image = UIImage.imageWithContentsOfFile(this) ?: return null
        return MPMediaItemArtwork(boundsSize = image.size) { image }
    }
}

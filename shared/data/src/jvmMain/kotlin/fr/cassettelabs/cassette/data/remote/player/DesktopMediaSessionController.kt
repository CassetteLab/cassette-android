package fr.cassettelabs.cassette.data.remote.player

import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.Track

class DesktopMediaSessionController : PlatformMediaSessionController {
    override fun setCallbacks(callbacks: PlatformMediaSessionCallbacks) = Unit

    override fun update(
        track: Track?,
        playbackState: PlaybackState,
    ) = Unit

    override fun dispose() = Unit
}

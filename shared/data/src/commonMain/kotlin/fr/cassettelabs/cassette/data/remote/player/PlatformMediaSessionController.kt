package fr.cassettelabs.cassette.data.remote.player

import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.Track

interface PlatformMediaSessionController {
    fun setCallbacks(callbacks: PlatformMediaSessionCallbacks)

    fun update(
        track: Track?,
        playbackState: PlaybackState,
    )

    fun dispose()
}

data class PlatformMediaSessionCallbacks(
    val play: () -> Unit,
    val pause: () -> Unit,
    val seekTo: (Long) -> Unit,
    val skipToNext: () -> Unit,
    val skipToPrevious: () -> Unit,
)

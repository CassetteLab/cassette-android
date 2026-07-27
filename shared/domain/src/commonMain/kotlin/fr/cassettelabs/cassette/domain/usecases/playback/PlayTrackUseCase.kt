package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class PlayTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(
        currentTrack: Track,
        contextTracks: List<Track> = emptyList(),
        context: PlaybackContext? = null,
    ) {
        playbackRepository.play(currentTrack = currentTrack, contextTracks = contextTracks, context = context)
    }
}

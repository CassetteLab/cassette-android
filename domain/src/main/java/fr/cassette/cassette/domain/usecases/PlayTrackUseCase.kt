package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackContext
import fr.cassette.cassette.domain.repositories.PlaybackRepository

class PlayTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(
        currentTrack: CurrentTrack,
        contextTracks: List<CurrentTrack> = emptyList(),
        context: PlaybackContext? = null,
    ) {
        playbackRepository.play(currentTrack = currentTrack, contextTracks = contextTracks, context = context)
    }
}

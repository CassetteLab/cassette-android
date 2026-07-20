package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.repositories.PlaybackRepository

class PlayTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(currentTrack: CurrentTrack) {
        playbackRepository.play(currentTrack)
    }
}

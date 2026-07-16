package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(currentTrack: CurrentTrack) {
        playbackRepository.setCurrentTrack(currentTrack)
    }
}

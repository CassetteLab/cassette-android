package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class SkipToNextTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke() {
        playbackRepository.skipToNext()
    }
}

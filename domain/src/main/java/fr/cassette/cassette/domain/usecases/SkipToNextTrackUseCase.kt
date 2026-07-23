package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SkipToNextTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke() {
        playbackRepository.skipToNext()
    }
}

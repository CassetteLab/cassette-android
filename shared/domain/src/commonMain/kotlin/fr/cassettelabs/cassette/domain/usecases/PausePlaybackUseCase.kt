package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class PausePlaybackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke() {
        playbackRepository.pause()
    }
}

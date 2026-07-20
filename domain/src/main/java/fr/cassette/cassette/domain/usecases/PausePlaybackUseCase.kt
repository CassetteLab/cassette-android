package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.PlaybackRepository

class PausePlaybackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke() {
        playbackRepository.pause()
    }
}

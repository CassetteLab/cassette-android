package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SeekPlaybackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(positionMs: Long) {
        playbackRepository.seekTo(positionMs)
    }
}

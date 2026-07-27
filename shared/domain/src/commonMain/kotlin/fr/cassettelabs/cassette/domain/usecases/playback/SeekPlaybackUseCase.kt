package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class SeekPlaybackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(positionMs: Long) {
        playbackRepository.seekTo(positionMs)
    }
}

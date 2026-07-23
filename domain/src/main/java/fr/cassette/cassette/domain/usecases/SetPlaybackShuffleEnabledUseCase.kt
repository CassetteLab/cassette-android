package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SetPlaybackShuffleEnabledUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(isEnabled: Boolean) {
        playbackRepository.setShuffleEnabled(isEnabled)
    }
}

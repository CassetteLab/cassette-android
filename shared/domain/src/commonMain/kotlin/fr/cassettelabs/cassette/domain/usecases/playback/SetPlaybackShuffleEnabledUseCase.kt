package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class SetPlaybackShuffleEnabledUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(isEnabled: Boolean) {
        playbackRepository.setShuffleEnabled(isEnabled)
    }
}

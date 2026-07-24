package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class SetPlaybackRepeatModeUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(repeatMode: RepeatMode) {
        playbackRepository.setRepeatMode(repeatMode)
    }
}

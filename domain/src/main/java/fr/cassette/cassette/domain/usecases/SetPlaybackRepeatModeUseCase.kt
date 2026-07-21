package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.RepeatMode
import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SetPlaybackRepeatModeUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(repeatMode: RepeatMode) {
        playbackRepository.setRepeatMode(repeatMode)
    }
}

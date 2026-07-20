package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.PlaybackState
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetPlaybackStateUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<PlaybackState> = playbackRepository.playbackState
}

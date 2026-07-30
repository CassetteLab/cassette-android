package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetPlaybackStateUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<PlaybackState> = playbackRepository.playbackState
}

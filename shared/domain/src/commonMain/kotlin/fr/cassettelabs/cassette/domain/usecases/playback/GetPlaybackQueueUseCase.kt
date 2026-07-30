package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

class GetPlaybackQueueUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<List<Track>> = playbackRepository.playbackQueue
}

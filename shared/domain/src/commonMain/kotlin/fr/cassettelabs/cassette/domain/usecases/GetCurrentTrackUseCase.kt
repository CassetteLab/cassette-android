package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

class GetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<Track?> = playbackRepository.currentTrack
}

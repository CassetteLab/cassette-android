package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<Track?> = playbackRepository.currentTrack
}

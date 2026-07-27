package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<CurrentTrack?> = playbackRepository.currentTrack
}

package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(): Flow<CurrentTrack?> = playbackRepository.currentTrack
}

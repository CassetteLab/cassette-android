package fr.cassettelabs.cassette.domain.usecases.starred

import fr.cassettelabs.cassette.domain.repositories.TrackRepository

class SetTrackStarredUseCase(
    private val trackRepository: TrackRepository,
) {
    suspend operator fun invoke(
        trackId: String,
        isStarred: Boolean,
    ) {
        trackRepository.setTrackStarred(trackId = trackId, isStarred = isStarred)
    }
}

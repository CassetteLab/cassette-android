package fr.cassettelabs.cassette.domain.usecases.playlistCreate

import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.TrackRepository

class GetAllTracksUseCase(
    private val trackRepository: TrackRepository,
) {
    suspend operator fun invoke(): List<Track> = trackRepository.getAllTracks()
}

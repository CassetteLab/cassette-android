package fr.cassettelabs.cassette.domain.usecases.album

import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetAlbumTracksUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String): List<Track> = albumRepository.getAlbumTracks(albumId)
}

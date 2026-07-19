package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.AlbumRepository

class GetAlbumTracksUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String): List<Track> {
        return albumRepository.getAlbumTracks(albumId)
    }
}

package fr.cassettelabs.cassette.domain.usecases.artistDetail

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetArtistAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(artistId: String): List<Album> = albumRepository.getArtistAlbums(artistId)
}

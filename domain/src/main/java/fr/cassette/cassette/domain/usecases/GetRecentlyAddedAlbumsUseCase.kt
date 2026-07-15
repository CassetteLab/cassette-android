package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.Album
import fr.cassette.cassette.domain.repositories.AlbumRepository

class GetRecentlyAddedAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(size: Int = DEFAULT_SIZE): List<Album> {
        return albumRepository.getRecentlyAddedAlbums(size)
    }

    private companion object {
        const val DEFAULT_SIZE = 60
    }
}

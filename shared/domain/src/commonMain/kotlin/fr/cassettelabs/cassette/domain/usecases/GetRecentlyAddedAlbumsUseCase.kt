package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetRecentlyAddedAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(size: Int = DEFAULT_SIZE): List<AlbumList> = albumRepository.getRecentlyAddedAlbums(size)

    private companion object {
        const val DEFAULT_SIZE = 60
    }
}

package fr.cassettelabs.cassette.domain.usecases.albumList

import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class RefreshAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        albumRepository.refreshAlbums()
    }
}

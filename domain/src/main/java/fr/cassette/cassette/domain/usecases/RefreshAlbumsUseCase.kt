package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.AlbumRepository

class RefreshAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        albumRepository.refreshAlbums()
    }
}

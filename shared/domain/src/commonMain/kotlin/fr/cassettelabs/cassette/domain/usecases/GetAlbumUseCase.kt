package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String): AlbumDetail = albumRepository.getAlbum(albumId)
}

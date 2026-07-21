package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.repositories.AlbumRepository

class GetAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String): AlbumDetail = albumRepository.getAlbum(albumId)
}

package fr.cassettelabs.cassette.domain.usecases.album

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: AlbumId): AlbumDetail = albumRepository.getAlbum(albumId)
}

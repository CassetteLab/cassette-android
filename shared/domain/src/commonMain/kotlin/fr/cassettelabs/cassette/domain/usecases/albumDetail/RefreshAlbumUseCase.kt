package fr.cassettelabs.cassette.domain.usecases.albumDetail

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class RefreshAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: AlbumId): Album = albumRepository.refreshAlbum(albumId)
}

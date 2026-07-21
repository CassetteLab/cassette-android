package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.repositories.AlbumRepository

class GetAlbumCoverArtUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        coverArtId: String,
        size: Int? = null,
        albumId: String? = null,
    ): AlbumCoverArt {
        return albumRepository.getAlbumCoverArt(coverArtId = coverArtId, size = size, albumId = albumId)
    }
}

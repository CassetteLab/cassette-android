package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetAlbumCoverArtUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        coverArtId: String,
        size: Int? = null,
        albumId: String? = null,
    ): AlbumCoverArt = albumRepository.getAlbumCoverArt(coverArtId = coverArtId, size = size, albumId = albumId)
}

package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.domain.repositories.AlbumRepository

class GetAlbumCoverArtRequestUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(coverArtId: String, size: Int? = null): AlbumCoverArtRequest {
        return albumRepository.getAlbumCoverArtRequest(coverArtId = coverArtId, size = size)
    }
}

package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAlbumCoverArtUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(
        coverArtId: String,
        size: Int? = null,
        albumId: String? = null,
    ): Flow<CoverArtLoadingStatus> = albumRepository.getAlbumCoverArt(coverArtId = coverArtId, size = size, albumId = albumId)
}

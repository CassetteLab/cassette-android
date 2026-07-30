package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistCoverArtUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(
        coverArtId: String,
        size: Int? = null,
        playlistId: String? = null,
    ): Flow<CoverArtLoadingStatus> = playlistRepository.getPlaylistCoverArt(coverArtId = coverArtId, size = size, playlistId = playlistId)
}

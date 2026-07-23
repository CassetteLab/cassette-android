package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.repositories.PlaylistRepository

class GetPlaylistCoverArtUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(
        coverArtId: String,
        size: Int? = null,
        playlistId: String? = null,
    ): AlbumCoverArt = playlistRepository.getPlaylistCoverArt(coverArtId = coverArtId, size = size, playlistId = playlistId)
}

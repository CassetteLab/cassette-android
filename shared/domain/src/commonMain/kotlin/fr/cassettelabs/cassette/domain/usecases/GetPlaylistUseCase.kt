package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.PlaylistDetail
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository

class GetPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String): PlaylistDetail = playlistRepository.getPlaylist(playlistId)
}

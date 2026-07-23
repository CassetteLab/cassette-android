package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.PlaylistDetail
import fr.cassette.cassette.domain.repositories.PlaylistRepository

class GetPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String): PlaylistDetail = playlistRepository.getPlaylist(playlistId)
}

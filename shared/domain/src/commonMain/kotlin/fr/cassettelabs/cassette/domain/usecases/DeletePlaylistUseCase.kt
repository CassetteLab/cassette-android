package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository

class DeletePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String) {
        playlistRepository.deletePlaylist(playlistId)
    }
}

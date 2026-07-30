package fr.cassettelabs.cassette.domain.usecases.playlistList

import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository

class RefreshPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke() {
        playlistRepository.refreshPlaylists()
    }
}

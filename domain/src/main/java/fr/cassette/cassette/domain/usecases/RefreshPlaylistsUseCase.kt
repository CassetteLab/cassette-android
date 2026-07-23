package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.PlaylistRepository

class RefreshPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke() {
        playlistRepository.refreshPlaylists()
    }
}

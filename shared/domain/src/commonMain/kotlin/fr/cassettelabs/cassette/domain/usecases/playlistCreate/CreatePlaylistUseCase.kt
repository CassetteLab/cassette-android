package fr.cassettelabs.cassette.domain.usecases.playlistCreate

import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository

class CreatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(name: String): Playlist = playlistRepository.createPlaylist(name)
}

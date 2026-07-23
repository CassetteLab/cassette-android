package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetAllPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(): Flow<List<PlaylistList>> = playlistRepository.getAllPlaylists()
}

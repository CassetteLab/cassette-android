package fr.cassettelabs.cassette.domain.usecases.playlistList

import fr.cassettelabs.cassette.domain.models.PlaylistList
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetAllPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(): Flow<List<PlaylistList>> = playlistRepository.getAllPlaylists()
}

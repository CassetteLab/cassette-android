package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.PlaylistList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllPlaylistsUseCase {
    operator fun invoke(): Flow<List<PlaylistList>> = flowOf(emptyList())
}

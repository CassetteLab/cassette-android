package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllAlbumsUseCase {
    operator fun invoke(): Flow<List<AlbumList>> {
        return flowOf(emptyList())
    }
}

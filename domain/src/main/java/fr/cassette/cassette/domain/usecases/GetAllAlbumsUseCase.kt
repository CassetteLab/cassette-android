package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<AlbumList>> {
        return albumRepository.getAllAlbums()
    }
}

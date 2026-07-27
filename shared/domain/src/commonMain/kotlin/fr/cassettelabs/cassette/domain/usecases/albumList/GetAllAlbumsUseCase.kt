package fr.cassettelabs.cassette.domain.usecases.albumList

import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<AlbumList>> = albumRepository.getAllAlbums()
}

package fr.cassettelabs.cassette.domain.usecases.albumList

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<Album>> = albumRepository.getAllAlbums()
}

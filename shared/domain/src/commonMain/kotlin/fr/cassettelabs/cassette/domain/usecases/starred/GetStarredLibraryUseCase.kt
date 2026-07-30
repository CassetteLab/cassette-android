package fr.cassettelabs.cassette.domain.usecases.starred

import fr.cassettelabs.cassette.domain.models.StarredLibrary
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetStarredLibraryUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(): StarredLibrary = albumRepository.getStarredLibrary()
}

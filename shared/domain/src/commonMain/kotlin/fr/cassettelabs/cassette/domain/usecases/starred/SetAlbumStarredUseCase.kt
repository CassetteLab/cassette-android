package fr.cassettelabs.cassette.domain.usecases.starred

import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class SetAlbumStarredUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumId: String,
        isStarred: Boolean,
    ) {
        albumRepository.setAlbumStarred(albumId = albumId, isStarred = isStarred)
    }
}

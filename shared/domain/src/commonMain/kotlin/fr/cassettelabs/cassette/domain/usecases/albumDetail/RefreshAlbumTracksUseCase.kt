package fr.cassettelabs.cassette.domain.usecases.albumDetail

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class RefreshAlbumTracksUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: AlbumId): List<Track> = albumRepository.refreshAlbumTracks(albumId)
}

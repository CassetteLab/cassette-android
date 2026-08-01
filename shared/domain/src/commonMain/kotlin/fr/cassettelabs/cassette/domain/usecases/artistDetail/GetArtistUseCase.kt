package fr.cassettelabs.cassette.domain.usecases.artistDetail

import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository

class GetArtistUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(artistId: String): Artist? = albumRepository.getArtist(artistId)
}

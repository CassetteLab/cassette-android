package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.PlaylistRepository

class GetPlaylistTracksUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String): List<Track> = playlistRepository.getPlaylistTracks(playlistId)
}

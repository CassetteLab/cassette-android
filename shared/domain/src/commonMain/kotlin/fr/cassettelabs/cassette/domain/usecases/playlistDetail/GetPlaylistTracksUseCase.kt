package fr.cassettelabs.cassette.domain.usecases.playlistDetail

import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository

class GetPlaylistTracksUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String): List<Track> = playlistRepository.getPlaylistTracks(playlistId)
}

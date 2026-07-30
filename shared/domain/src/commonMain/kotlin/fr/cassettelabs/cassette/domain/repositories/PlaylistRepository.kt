package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylist(playlistId: String): Playlist

    suspend fun getPlaylistTracks(playlistId: String): List<Track>

    suspend fun refreshPlaylists()

    fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int? = null,
        playlistId: String? = null,
    ): Flow<CoverArtLoadingStatus>
}

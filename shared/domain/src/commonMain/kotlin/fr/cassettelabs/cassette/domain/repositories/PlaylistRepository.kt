package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.PlaylistDetail
import fr.cassettelabs.cassette.domain.models.PlaylistList
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistList>>

    suspend fun getPlaylist(playlistId: String): PlaylistDetail

    suspend fun getPlaylistTracks(playlistId: String): List<Track>

    suspend fun refreshPlaylists()

    suspend fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int? = null,
        playlistId: String? = null,
    ): AlbumCoverArt
}

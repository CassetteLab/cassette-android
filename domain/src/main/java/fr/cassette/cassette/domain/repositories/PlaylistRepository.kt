package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.PlaylistDetail
import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.domain.models.Track
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

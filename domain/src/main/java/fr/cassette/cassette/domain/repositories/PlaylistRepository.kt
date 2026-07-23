package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.PlaylistList
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistList>>

    suspend fun refreshPlaylists()

    suspend fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int? = null,
        playlistId: String? = null,
    ): AlbumCoverArt
}

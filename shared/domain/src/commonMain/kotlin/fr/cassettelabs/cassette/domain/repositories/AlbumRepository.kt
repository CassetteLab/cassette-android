package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<Album>

    suspend fun getAlbum(albumId: AlbumId): Album

    suspend fun getAlbumTracks(albumId: AlbumId): List<Track>

    suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int? = null,
        albumId: AlbumId? = null,
    ): AlbumCoverArt

    fun getAllAlbums(): Flow<List<Album>>

    suspend fun refreshAlbums()
}

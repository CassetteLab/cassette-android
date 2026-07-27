package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList>

    suspend fun getAlbum(albumId: String): AlbumDetail

    suspend fun getAlbumTracks(albumId: String): List<Track>

    suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int? = null,
        albumId: String? = null,
    ): AlbumCoverArt

    fun getAllAlbums(): Flow<List<AlbumList>>

    suspend fun refreshAlbums()
}

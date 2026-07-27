package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList>

    suspend fun getAlbum(albumId: AlbumId): AlbumDetail

    suspend fun getAlbumTracks(albumId: AlbumId): List<Track>

    suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int? = null,
        albumId: AlbumId? = null,
    ): AlbumCoverArt

    fun getAllAlbums(): Flow<List<AlbumList>>

    suspend fun refreshAlbums()
}

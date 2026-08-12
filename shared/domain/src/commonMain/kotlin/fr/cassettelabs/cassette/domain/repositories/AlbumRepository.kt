package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.StarredLibrary
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<Album>

    suspend fun getStarredLibrary(): StarredLibrary

    suspend fun setAlbumStarred(
        albumId: AlbumId,
        isStarred: Boolean,
    )

    suspend fun getAlbum(albumId: AlbumId): Album?

    suspend fun refreshAlbum(albumId: AlbumId): Album

    suspend fun getAlbumTracks(albumId: AlbumId): List<Track>

    suspend fun refreshAlbumTracks(albumId: AlbumId): List<Track>

    fun getAlbumCoverArt(
        coverArtId: String,
        size: Int? = null,
        albumId: AlbumId? = null,
    ): Flow<CoverArtLoadingStatus>

    fun getAllAlbums(): Flow<List<Album>>

    suspend fun refreshAlbums()

    suspend fun getArtist(artistId: String): Artist?

    suspend fun refreshArtist(artistId: String): Artist

    suspend fun getArtistAlbums(artistId: String): List<Album>
}

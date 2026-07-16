package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.domain.models.AlbumList

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList>
    suspend fun getAlbum(albumId: String): AlbumDetail
    suspend fun getAlbumCoverArtRequest(coverArtId: String, size: Int? = null): AlbumCoverArtRequest
}

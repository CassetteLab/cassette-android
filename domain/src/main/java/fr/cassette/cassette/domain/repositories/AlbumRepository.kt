package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.AlbumList

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList>
    suspend fun getAlbum(albumId: String): AlbumDetail
    suspend fun getAlbumCoverArt(coverArtId: String, size: Int? = null): AlbumCoverArt
}

package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.Album

interface AlbumRepository {
    suspend fun getRecentlyAddedAlbums(size: Int): List<Album>
    suspend fun getAlbum(albumId: String): Album
}

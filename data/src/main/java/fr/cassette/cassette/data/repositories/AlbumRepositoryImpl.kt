package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassette.cassette.domain.models.Album
import fr.cassette.cassette.domain.repositories.AlbumRepository

internal class AlbumRepositoryImpl(
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
) : AlbumRepository {
    override suspend fun getRecentlyAddedAlbums(size: Int): List<Album> {
        return albumRemoteDataSource.getRecentlyAddedAlbums(size)
    }
}

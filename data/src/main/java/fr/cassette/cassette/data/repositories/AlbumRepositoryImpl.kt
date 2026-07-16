package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.domain.repositories.AlbumRepository

internal class AlbumRepositoryImpl(
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
) : AlbumRepository {
    override suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> {
        return albumRemoteDataSource.getRecentlyAddedAlbums(size)
    }

    override suspend fun getAlbum(albumId: String): AlbumDetail {
        return albumRemoteDataSource.getAlbum(albumId)
    }

    override suspend fun getAlbumCoverArt(coverArtId: String, size: Int?): AlbumCoverArt {
        return albumRemoteDataSource.getAlbumCoverArt(coverArtId = coverArtId, size = size)
    }
}

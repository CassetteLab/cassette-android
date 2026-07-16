package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
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

    override suspend fun getAlbumCoverArtRequest(coverArtId: String, size: Int?): AlbumCoverArtRequest {
        return albumRemoteDataSource.getAlbumCoverArtRequest(coverArtId = coverArtId, size = size)
    }
}

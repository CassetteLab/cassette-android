package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AlbumRepositoryImpl(
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val serverConfigurationDao: ServerConfigurationDao,
) : AlbumRepository {
    override suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> =
        albumRemoteDataSource.getRecentlyAddedAlbums(size).map { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalData =
                album.copy(
                    coverArtFilePath = localAlbum?.coverArtFilePath,
                    seedColor = localAlbum?.seedColor,
                )
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
            albumWithLocalData
        }

    override suspend fun getAlbum(albumId: String): AlbumDetail {
        val localAlbum = albumDao.getAlbum(albumId)
        if (localAlbum != null) {
            return localAlbum.toDetailDomain()
        }

        val album =
            albumRemoteDataSource
                .getAlbum(albumId)
                .copy(tracks = emptyList())
        albumDao.insertAlbum(album.toEntity(localAlbum?.serverConfigurationId))
        return album
    }

    override suspend fun getAlbumTracks(albumId: String): List<Track> = albumRemoteDataSource.getAlbumTracks(albumId)

    override suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
        albumId: String?,
    ): AlbumCoverArt = albumRemoteDataSource.getAlbumCoverArt(coverArtId = coverArtId, size = size)

    override fun getAllAlbums(): Flow<List<AlbumList>> =
        albumDao.getAllAlbums().map { entities ->
            entities.map { it.toListDomain() }
        }

    override suspend fun refreshAlbums() {
        val remoteAlbums = albumRemoteDataSource.getAllAlbums(size = ALL_ALBUMS_SIZE)
        val serverConfigurationId = currentServerConfigurationId()
        remoteAlbums.forEach { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalData =
                album.copy(
                    coverArtFilePath = localAlbum?.coverArtFilePath,
                    seedColor = localAlbum?.seedColor,
                )
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
    }

    private suspend fun AlbumList.toEntity(existingServerConfigurationId: Long?): AlbumEntity =
        AlbumEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
            seedColor = seedColor,
        )

    private suspend fun AlbumDetail.toEntity(existingServerConfigurationId: Long?): AlbumEntity =
        AlbumEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
            seedColor = seedColor,
        )

    private fun AlbumEntity.toDetailDomain(): AlbumDetail =
        AlbumDetail(
            id = id,
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
            tracks = emptyList(),
            seedColor = seedColor,
        )

    private fun AlbumEntity.toListDomain(): AlbumList =
        AlbumList(
            id = id,
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
            seedColor = seedColor,
        )

    private suspend fun currentServerConfigurationId(): Long =
        serverConfigurationDao.getServerConfiguration()?.serverConfiguration?.id
            ?: throw IllegalStateException("No server configuration found")

    private companion object {
        const val ALL_ALBUMS_SIZE = 500
    }
}

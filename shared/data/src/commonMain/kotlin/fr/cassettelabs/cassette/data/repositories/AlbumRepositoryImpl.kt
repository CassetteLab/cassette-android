package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AlbumRepositoryImpl(
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val serverConfigurationDao: ServerConfigurationDao,
) : AlbumRepository {
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

    private fun AlbumList.toEntity(serverConfigurationId: Long): AlbumEntity =
        AlbumEntity(
            id = id,
            serverConfigurationId = serverConfigurationId,
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
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

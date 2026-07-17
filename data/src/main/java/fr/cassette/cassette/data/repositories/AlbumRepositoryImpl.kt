package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.data.local.dao.AlbumDao
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.entities.AlbumEntity
import fr.cassette.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.domain.repositories.AlbumRepository
import java.io.File

internal class AlbumRepositoryImpl(
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val serverConfigurationDao: ServerConfigurationDao,
) : AlbumRepository {
    override suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> {
        return albumRemoteDataSource.getRecentlyAddedAlbums(size).map { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalCoverArt = album.copy(coverArtFilePath = localAlbum?.validCoverArtFilePath())
            albumDao.insertAlbum(albumWithLocalCoverArt.toEntity(localAlbum?.serverConfigurationId))
            albumWithLocalCoverArt
        }
    }

    override suspend fun getAlbum(albumId: String): AlbumDetail {
        val localAlbum = albumDao.getAlbum(albumId)
        val album = albumRemoteDataSource.getAlbum(albumId)
            .copy(coverArtFilePath = localAlbum?.validCoverArtFilePath())
        albumDao.insertAlbum(album.toEntity(localAlbum?.serverConfigurationId))
        return album
    }

    override suspend fun getAlbumCoverArt(coverArtId: String, size: Int?, albumId: String?): AlbumCoverArt {
        albumId?.let { id ->
            albumDao.getAlbum(id)?.validCoverArtFilePath()?.let { filePath ->
                return AlbumCoverArt(filePath = filePath)
            }
        }

        return albumRemoteDataSource.getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                albumId?.let { id -> albumDao.updateCoverArtFilePath(albumId = id, coverArtFilePath = coverArt.filePath) }
            }
    }

    private suspend fun AlbumList.toEntity(existingServerConfigurationId: Long?): AlbumEntity {
        return AlbumEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
        )
    }

    private suspend fun AlbumDetail.toEntity(existingServerConfigurationId: Long?): AlbumEntity {
        return AlbumEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            artist = artist,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            created = created,
        )
    }

    private suspend fun currentServerConfigurationId(): Long {
        return serverConfigurationDao.getServerConfiguration()?.serverConfiguration?.id
            ?: throw IllegalStateException("No server configuration found")
    }

    private fun AlbumEntity.validCoverArtFilePath(): String? {
        return coverArtFilePath?.takeIf { filePath -> File(filePath).exists() }
    }
}

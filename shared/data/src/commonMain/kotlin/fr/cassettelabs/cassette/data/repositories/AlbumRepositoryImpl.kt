package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
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
    private val trackDao: TrackDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
) : AlbumRepository {
    override suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> =
        albumRemoteDataSource.getRecentlyAddedAlbums(size).map { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalData =
                album.copy(
                    coverArtFilePath = localAlbum?.validCoverArtFilePath(),
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

    override suspend fun getAlbumTracks(albumId: String): List<Track> {
        val localTracks = trackDao.getAlbumTracks(albumId)
        if (localTracks.isNotEmpty()) {
            return localTracks.map { it.toDomain() }
        }

        val localAlbum = albumDao.getAlbum(albumId)
        val remoteAlbum =
            albumRemoteDataSource
                .getAlbum(albumId)
                .copy(coverArtFilePath = localAlbum?.validCoverArtFilePath())
        albumDao.insertAlbum(remoteAlbum.toEntity(localAlbum?.serverConfigurationId))
        trackDao.deleteAlbumTracks(albumId)
        trackDao.insertTracks(remoteAlbum.tracks.map { track -> track.toEntity(albumId) })
        return remoteAlbum.tracks
    }

    override suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
        albumId: String?,
    ): AlbumCoverArt {
        albumId?.let { id ->
            albumDao.getAlbum(id)?.validCoverArtFilePath()?.let { filePath ->
                return AlbumCoverArt(filePath = filePath)
            }
        }

        return albumRemoteDataSource
            .getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                albumId?.let { id ->
                    albumDao.updateCoverArtFilePath(albumId = id, coverArtFilePath = coverArt.filePath)
                    extractAndSaveSeedColor(id, coverArt.filePath)
                }
            }
    }

    private suspend fun extractAndSaveSeedColor(
        albumId: String,
        filePath: String,
    ) {
        val existingSeedColor = albumDao.getAlbum(albumId)?.seedColor
        if (existingSeedColor != null) return

        try {
            coverArtProcessor.extractSeedColor(filePath)?.let { seedColor ->
                albumDao.updateSeedColor(albumId, seedColor)
            }
        } catch (_: Exception) {
        }
    }

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
                    coverArtFilePath = localAlbum?.validCoverArtFilePath(),
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
            coverArtFilePath = validCoverArtFilePath(),
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
            coverArtFilePath = validCoverArtFilePath(),
            created = created,
            seedColor = seedColor,
        )

    private fun Track.toEntity(albumId: String): TrackEntity =
        TrackEntity(
            id = id,
            albumId = albumId,
            title = title,
            artist = artist,
            trackNumber = trackNumber,
            durationSeconds = durationSeconds,
        )

    private fun TrackEntity.toDomain(): Track =
        Track(
            id = id,
            title = title,
            artist = artist,
            trackNumber = trackNumber,
            durationSeconds = durationSeconds,
        )

    private suspend fun currentServerConfigurationId(): Long =
        serverConfigurationDao.getServerConfiguration()?.serverConfiguration?.id
            ?: throw IllegalStateException("No server configuration found")

    private fun AlbumEntity.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }

    private companion object {
        const val ALL_ALBUMS_SIZE = 500
    }
}

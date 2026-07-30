package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.core.coroutines.CoroutineDispatchers
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.StarredLibrary
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class AlbumRepositoryImpl(
    private val logger: Logger,
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
    private val coroutineDispatchers: CoroutineDispatchers,
) : AlbumRepository {

    init {
        logger.init("AlbumRepositoryImpl")
    }

    override suspend fun getRecentlyAddedAlbums(size: Int): List<Album> =
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

    override suspend fun getStarredLibrary(): StarredLibrary {
        val starredLibrary = albumRemoteDataSource.getStarredLibrary()
        val serverConfigurationId = currentServerConfigurationId()

        starredLibrary.albums.forEach { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalData = album.withLocalAlbumData(localAlbum)
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
        return starredLibrary
    }

    override suspend fun getAlbum(albumId: AlbumId): Album? {
        val localAlbum = albumDao.getAlbum(albumId)
        if (localAlbum == null) {
            logger.w("Can't find album with id: $albumId")
            return null
        }

        return localAlbum.toDomain()
    }

    override suspend fun refreshAlbum(albumId: AlbumId): Album {
        val localAlbum = albumDao.getAlbum(albumId)
        val album = albumRemoteDataSource.getAlbum(albumId)
        val albumWithLocalData = album.withLocalAlbumData(localAlbum)
        albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
        return albumWithLocalData
    }

    override suspend fun getAlbumTracks(albumId: String): List<Track> {
        val localTracks = trackDao.getAlbumTracks(albumId)
        if (localTracks.isNotEmpty()) {
            return localTracks.map { it.toDomain() }
        }

        val localAlbum = albumDao.getAlbum(albumId)
        val (remoteAlbum, remoteTracks) = albumRemoteDataSource.getAlbumWithTracks(albumId)
        val albumWithLocalData = remoteAlbum.withLocalAlbumData(localAlbum)
        albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
        trackDao.deleteAlbumTracks(albumId)
        trackDao.insertTracks(remoteTracks.map { track -> track.toEntity(albumId) })
        return remoteTracks
    }

    override suspend fun refreshAlbumTracks(albumId: AlbumId): List<Track> {
        val remoteTracks = albumRemoteDataSource.getAlbumTracks(albumId)
        trackDao.deleteAlbumTracks(albumId)
        trackDao.insertTracks(remoteTracks.map { track -> track.toEntity(albumId) })
        return remoteTracks
    }

    override fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
        albumId: String?,
    ): Flow<CoverArtLoadingStatus> = flow {
        albumId?.let { id ->
            albumDao.getAlbum(id)?.validCoverArtFilePath()?.let { filePath ->
                emit(CoverArtLoadingStatus.Loaded(filePath))
                return@flow
            }
        }

        emit(CoverArtLoadingStatus.Loading)

        val coverArt = albumRemoteDataSource
            .getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                albumId?.let { id ->
                    albumDao.updateCoverArtFilePath(albumId = id, coverArtFilePath = coverArt.filePath)
                    extractAndSaveSeedColor(id, coverArt.filePath)
                }
            }
        emit(CoverArtLoadingStatus.Loaded(coverArt.filePath))
    }.flowOn(coroutineDispatchers.io).catch { throwable ->
        emit(CoverArtLoadingStatus.Error(throwable))
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

    override fun getAllAlbums(): Flow<List<Album>> =
        albumDao.getAllAlbums().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun refreshAlbums() {
        val remoteAlbums = albumRemoteDataSource.getAllAlbums(size = ALL_ALBUMS_SIZE)
        val serverConfigurationId = currentServerConfigurationId()
        remoteAlbums.forEach { album ->
            val localAlbum = albumDao.getAlbum(album.id)
            val albumWithLocalData =
                album.withLocalAlbumData(localAlbum)
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
    }

    private fun Album.withLocalAlbumData(localAlbum: AlbumEntity?): Album {
        val canReuseLocalArtworkData = localAlbum?.coverArt == coverArt
        return copy(
            coverArtFilePath = localAlbum?.validCoverArtFilePath().takeIf { canReuseLocalArtworkData },
            seedColor = localAlbum?.seedColor.takeIf { canReuseLocalArtworkData },
        )
    }

    private suspend fun Album.toEntity(existingServerConfigurationId: Long?): AlbumEntity =
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

    private fun AlbumEntity.toDomain(): Album =
        Album(
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
            albumName = albumName,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            starredAt = starredAt,
        )

    private fun TrackEntity.toDomain(): Track =
        Track(
            id = id,
            title = title,
            artist = artist,
            trackNumber = trackNumber,
            durationSeconds = durationSeconds,
            albumId = albumId,
            albumName = albumName,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            starredAt = starredAt,
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

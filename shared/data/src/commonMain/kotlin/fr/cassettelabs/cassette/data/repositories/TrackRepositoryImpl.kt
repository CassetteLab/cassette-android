package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.remote.datasources.TrackRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.TrackRepository

internal class TrackRepositoryImpl(
    private val trackRemoteDataSource: TrackRemoteDataSourceImpl,
    private val trackDao: TrackDao,
) : TrackRepository {
    override suspend fun setTrackStarred(
        trackId: String,
        isStarred: Boolean,
    ) {
        trackRemoteDataSource.setTrackStarred(trackId = trackId, isStarred = isStarred)
        trackDao.updateStarredAt(
            trackId = trackId,
            starredAt = currentTimeMillis().toString().takeIf { isStarred },
        )
    }

    override suspend fun getAllTracks(): List<Track> =
        trackDao.getAllTracks(COVER_ART_SIZE)

    private companion object {
        const val COVER_ART_SIZE = 900
    }
}

package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.Track

interface TrackRepository {
    suspend fun setTrackStarred(
        trackId: String,
        isStarred: Boolean,
    )

    suspend fun getAllTracks(): List<Track>
}

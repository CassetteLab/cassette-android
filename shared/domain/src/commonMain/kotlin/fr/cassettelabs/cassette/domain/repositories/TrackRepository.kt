package fr.cassettelabs.cassette.domain.repositories

interface TrackRepository {
    suspend fun setTrackStarred(
        trackId: String,
        isStarred: Boolean,
    )
}

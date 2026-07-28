package fr.cassettelabs.cassette.data.local.embeddeds

internal data class PlaybackQueueItemWithTrack(
    val id: String,
    val sessionId: String,
    val section: String,
    val source: String,
    val position: Int,
    val trackId: String,
    val addedAt: Long,
    val title: String,
    val artist: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
    val albumId: String?,
    val albumName: String?,
    val coverArtId: String?,
    val coverArtFilePath: String?,
)

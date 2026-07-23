package fr.cassette.cassette.data.local.embeddeds

import fr.cassette.cassette.data.local.entities.PlaybackQueueItemSource
import fr.cassette.cassette.data.local.entities.PlaybackQueueSection

internal data class PlaybackQueueItemWithTrack(
    val id: String,
    val sessionId: String,
    val section: PlaybackQueueSection,
    val source: PlaybackQueueItemSource,
    val position: Int,
    val trackId: String,
    val addedAt: Long,
    val title: String,
    val artist: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
    val albumId: String,
    val albumName: String,
    val coverArtId: String?,
    val coverArtFilePath: String?,
)

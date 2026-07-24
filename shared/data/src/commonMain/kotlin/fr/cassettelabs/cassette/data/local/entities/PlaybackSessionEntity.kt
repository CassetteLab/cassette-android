package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_sessions")
internal data class PlaybackSessionEntity(
    @PrimaryKey
    val id: String,
    val currentPositionMs: Long,
    val contextType: String?,
    val contextId: String?,
    val isShuffleEnabled: Boolean,
    val repeatMode: String,
    val updatedAt: Long,
)

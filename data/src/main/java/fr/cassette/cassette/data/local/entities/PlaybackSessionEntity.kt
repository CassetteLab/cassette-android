package fr.cassette.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.cassette.cassette.domain.models.PlaybackContextType
import fr.cassette.cassette.domain.models.RepeatMode

@Entity(tableName = "playback_sessions")
internal data class PlaybackSessionEntity(
    @PrimaryKey
    val id: String,
    val currentPositionMs: Long,
    val contextType: PlaybackContextType?,
    val contextId: String?,
    val isShuffleEnabled: Boolean,
    val repeatMode: RepeatMode,
    val updatedAt: Long,
)

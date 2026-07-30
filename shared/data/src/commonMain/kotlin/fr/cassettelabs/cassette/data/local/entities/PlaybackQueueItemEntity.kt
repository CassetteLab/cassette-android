package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playback_queue_items",
    foreignKeys = [
        ForeignKey(
            entity = PlaybackSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId"), Index("section", "position")],
)
internal data class PlaybackQueueItemEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val section: String,
    val source: String,
    val position: Int,
    val trackId: String,
    val addedAt: Long,
)

internal enum class PlaybackQueueSection {
    History,
    Current,
    UserQueue,
    ContextQueue,
}

internal enum class PlaybackQueueItemSource {
    Context,
    UserAdded,
}

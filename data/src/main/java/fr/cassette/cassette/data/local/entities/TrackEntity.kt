package fr.cassette.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("albumId")],
)
internal data class TrackEntity(
    @PrimaryKey
    val id: String,
    val albumId: String,
    val title: String,
    val artist: String? = null,
    val trackNumber: Int? = null,
    val durationSeconds: Int? = null,
)

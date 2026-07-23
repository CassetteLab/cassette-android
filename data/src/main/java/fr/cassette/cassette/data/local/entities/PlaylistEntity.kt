package fr.cassette.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    foreignKeys = [
        ForeignKey(
            entity = ServerConfigurationEntity::class,
            parentColumns = ["id"],
            childColumns = ["serverConfigurationId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("serverConfigurationId")],
)
internal data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val trackCount: Int = 0,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
    val created: String? = null,
    val seedColor: Int? = null,
)

package fr.cassette.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albums",
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
internal data class AlbumEntity(
    @PrimaryKey
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val artist: String? = null,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
    val created: String? = null,
)

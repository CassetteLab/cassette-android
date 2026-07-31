package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "artists",
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
internal data class ArtistEntity(
    @PrimaryKey
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val albumCount: Int = 0,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
)

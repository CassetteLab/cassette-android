package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cover_arts",
    primaryKeys = ["serverConfigurationId", "coverArtId", "size"],
    foreignKeys = [
        ForeignKey(
            entity = ServerConfigurationEntity::class,
            parentColumns = ["id"],
            childColumns = ["serverConfigurationId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("serverConfigurationId"), Index("coverArtId")],
)
internal data class CoverArtEntity(
    val serverConfigurationId: Long,
    val coverArtId: String,
    val size: Int,
    val filePath: String,
    val updatedAt: Long,
)

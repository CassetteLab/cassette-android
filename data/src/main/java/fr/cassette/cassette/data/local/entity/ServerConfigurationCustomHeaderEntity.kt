package fr.cassette.cassette.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "server_configuration_custom_headers",
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
internal data class ServerConfigurationCustomHeaderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverConfigurationId: Long,
    val name: String,
    val value: String,
)

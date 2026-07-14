package fr.cassette.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_configurations")
internal data class ServerConfigurationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverUrl: String,
    val username: String,
    val password: String,
)

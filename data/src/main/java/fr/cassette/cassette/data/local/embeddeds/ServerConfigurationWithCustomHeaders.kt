package fr.cassette.cassette.data.local.embeddeds

import androidx.room.Embedded
import androidx.room.Relation
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity

internal data class ServerConfigurationWithCustomHeaders(
    @Embedded val serverConfiguration: ServerConfigurationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "serverConfigurationId",
    )
    val customHeaders: List<ServerConfigurationCustomHeaderEntity>,
)

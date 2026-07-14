package fr.cassette.cassette.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import fr.cassette.cassette.data.local.entity.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entity.ServerConfigurationEntity

internal data class ServerConfigurationWithCustomHeaders(
    @Embedded val serverConfiguration: ServerConfigurationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "serverConfigurationId",
    )
    val customHeaders: List<ServerConfigurationCustomHeaderEntity>,
)

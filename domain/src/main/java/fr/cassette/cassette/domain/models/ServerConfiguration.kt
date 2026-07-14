package fr.cassette.cassette.domain.models

data class ServerConfiguration(
    val serverUrl: String,
    val username: String,
    val password: String,
    val customHeaders: List<ServerConfigurationCustomHeader> = emptyList(),
)



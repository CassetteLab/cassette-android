package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.data.local.dao.LocalDataDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.embeddeds.ServerConfigurationWithCustomHeaders
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.dto.PingResponseDto
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.data.remote.ktor.md5
import fr.cassettelabs.cassette.domain.models.ServerConfiguration
import fr.cassettelabs.cassette.domain.models.ServerConfigurationCustomHeader
import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal class ServerConfigurationRepositoryImpl(
    private val localDataDao: LocalDataDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val cipherHelper: CipherHelper,
    private val httpClient: HttpClient,
    private val coverArtProcessor: CoverArtProcessor,
) : ServerConfigurationRepository {
    override suspend fun pingServer(serverConfiguration: ServerConfiguration) {
        val salt = currentTimeMillis().toString(16)
        val response =
            httpClient
                .get("${serverConfiguration.serverUrl.trimEnd('/')}/rest/ping.view") {
                    parameter("u", serverConfiguration.username)
                    parameter("t", md5(serverConfiguration.password + salt))
                    parameter("s", salt)
                    parameter("f", "json")

                    serverConfiguration.customHeaders
                        .filter { it.name.isNotBlank() }
                        .forEach { customHeader ->
                            header(customHeader.name, customHeader.value)
                        }
                }.body<PingResponseDto>()

        if (response.subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic ping failed")
        }
    }

    override suspend fun getServerConfiguration(): ServerConfiguration? = serverConfigurationDao.getServerConfiguration()?.toDomain()

    override suspend fun saveServerConfiguration(serverConfiguration: ServerConfiguration) {
        serverConfigurationDao.insertServerConfigurationWithCustomHeaders(
            serverConfiguration =
                ServerConfigurationEntity(
                    serverUrl = serverConfiguration.serverUrl,
                    username = serverConfiguration.username,
                    encryptedPassword = cipherHelper.encrypt(serverConfiguration.password),
                ),
            customHeaders =
                serverConfiguration.customHeaders.map { customHeader ->
                    ServerConfigurationCustomHeaderEntity(
                        serverConfigurationId = 0,
                        name = customHeader.name,
                        encryptedValue = cipherHelper.encrypt(customHeader.value),
                    )
                },
        )
    }

    override suspend fun hasServerConfiguration(): Boolean = serverConfigurationDao.hasServerConfiguration()

    override suspend fun logout() {
        localDataDao.deleteAllLocalData()
        coverArtProcessor.clearCache()
    }

    private fun ServerConfigurationWithCustomHeaders.toDomain(): ServerConfiguration =
        ServerConfiguration(
            serverUrl = serverConfiguration.serverUrl,
            username = serverConfiguration.username,
            password = cipherHelper.decrypt(serverConfiguration.encryptedPassword),
            customHeaders =
                customHeaders.map { customHeader ->
                    ServerConfigurationCustomHeader(
                        name = customHeader.name,
                        value = cipherHelper.decrypt(customHeader.encryptedValue),
                    )
                },
        )
}

package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import org.json.JSONObject
import java.security.MessageDigest

internal class ServerConfigurationRepositoryImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val cipherHelper: CipherHelper,
    private val httpClient: HttpClient,
) : ServerConfigurationRepository {
    override suspend fun pingServer(serverConfiguration: ServerConfiguration) {
        val salt = System.currentTimeMillis().toString(16)
        val responseBody = httpClient.get("${serverConfiguration.serverUrl.trimEnd('/')}/rest/ping.view") {
            parameter("u", serverConfiguration.username)
            parameter("t", md5(serverConfiguration.password + salt))
            parameter("s", salt)

            serverConfiguration.customHeaders
                .filter { it.name.isNotBlank() }
                .forEach { customHeader ->
                    header(customHeader.name, customHeader.value)
                }
        }.body<String>()

        val subsonicResponse = JSONObject(responseBody).getJSONObject("subsonic-response")
        if (subsonicResponse.getString("status") != "ok") {
            throw IllegalStateException("Subsonic ping failed")
        }
    }

    private fun md5(value: String): String = MessageDigest.getInstance("MD5")
        .digest(value.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }

    override suspend fun saveServerConfiguration(serverConfiguration: ServerConfiguration) {
        serverConfigurationDao.insertServerConfigurationWithCustomHeaders(
            serverConfiguration = ServerConfigurationEntity(
                serverUrl = serverConfiguration.serverUrl,
                username = serverConfiguration.username,
                encryptedPassword = cipherHelper.encrypt(serverConfiguration.password),
            ),
            customHeaders = serverConfiguration.customHeaders.map { customHeader ->
                ServerConfigurationCustomHeaderEntity(
                    serverConfigurationId = 0,
                    name = customHeader.name,
                    encryptedValue = cipherHelper.encrypt(customHeader.value),
                )
            },
        )
    }

    override suspend fun hasServerConfiguration(): Boolean {
        return serverConfigurationDao.hasServerConfiguration()
    }
}

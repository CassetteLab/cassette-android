package fr.cassette.cassette.data.remote.ktor

import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.ktor.plugins.cassetteRequestAuthenticationPlugin
import fr.cassette.cassette.data.remote.ktor.plugins.cassetteRequestDefaultsPlugin
import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.models.ServerConfigurationCustomHeader
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class KtorClientProviderImpl(
    private val applicationLogger: fr.cassette.cassette.core.logger.Logger,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val cipherHelper: CipherHelper,
) : KtorClientProvider {

    override fun getClient(): HttpClient = HttpClient(Android) {
        expectSuccess = true

        install(cassetteRequestDefaultsPlugin)
        install(cassetteRequestAuthenticationPlugin) {
            serverConfigurationProvider = {
                serverConfigurationDao.getFirstServerConfiguration()?.let { configuration ->
                    val server = configuration.serverConfiguration
                    ServerConfiguration(
                        serverUrl = server.serverUrl,
                        username = server.username,
                        password = cipherHelper.decrypt(server.encryptedPassword),
                        customHeaders = configuration.customHeaders.map { customHeader ->
                            ServerConfigurationCustomHeader(
                                name = customHeader.name,
                                value = cipherHelper.decrypt(customHeader.encryptedValue),
                            )
                        },
                    )
                }
            }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        install(Logging){
            logger = object: Logger {
                override fun log(message: String) {
                    applicationLogger.d(message)
                }
            }
        }
    }
}

package fr.cassette.cassette.data.remote.ktor

import fr.cassette.cassette.data.remote.ktor.plugins.CassetteRequestAuthenticationPluginProvider
import fr.cassette.cassette.data.remote.ktor.plugins.CassetteRequestDefaultsPluginProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class KtorClientProviderImpl(
    private val applicationLogger: fr.cassette.cassette.core.logger.Logger,
    private val cassetteRequestAuthenticationPluginProvider: CassetteRequestAuthenticationPluginProvider,
    private val cassetteRequestDefaultsPluginProvider: CassetteRequestDefaultsPluginProvider,
) : KtorClientProvider {
    override fun getClient(): HttpClient =
        HttpClient(Android) {
            expectSuccess = true

            install(cassetteRequestAuthenticationPluginProvider.getPlugin())
            install(cassetteRequestDefaultsPluginProvider.getPlugin())
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    },
                )
            }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            applicationLogger.d(message)
                        }
                    }
            }
        }
}

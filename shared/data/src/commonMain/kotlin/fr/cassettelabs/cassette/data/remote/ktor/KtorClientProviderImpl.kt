package fr.cassettelabs.cassette.data.remote.ktor

import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestAuthenticationPluginProvider
import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestDefaultsPluginProvider
import fr.cassettelabs.cassette.data.remote.ktor.plugins.LoggerPluginProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class KtorClientProviderImpl(
    private val applicationLogger: fr.cassettelabs.cassette.core.logger.Logger,
    private val cassetteRequestAuthenticationPluginProvider: CassetteRequestAuthenticationPluginProvider,
    private val cassetteRequestDefaultsPluginProvider: CassetteRequestDefaultsPluginProvider,
    private val loggerPluginProvider: LoggerPluginProvider,
) : KtorClientProvider {
    override fun getClient(): HttpClient =
        HttpClient(ktorClientEngineFactory()) {
            expectSuccess = true

            install(cassetteRequestAuthenticationPluginProvider.getPlugin())
            install(cassetteRequestDefaultsPluginProvider.getPlugin())
            install(loggerPluginProvider.getPlugin()) {
                logger = applicationLogger
                sanitizeParameter { it == "t" }
                sanitizeParameter { it == "u" }
                sanitizeParameter { it == "s" }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    },
                )
            }
        }
}

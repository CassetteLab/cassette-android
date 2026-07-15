package fr.cassette.cassette.data.remote.ktor

import fr.cassette.cassette.data.remote.ktor.plugins.cassetteRequestDefaultsPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

internal class KtorClientProviderImpl(
    private val applicationLogger: fr.cassette.cassette.core.logger.Logger
) : KtorClientProvider {

    override fun getClient(): HttpClient = HttpClient(Android) {
        expectSuccess = true

        install(cassetteRequestDefaultsPlugin)
        install(Logging){
            logger = object: Logger {
                override fun log(message: String) {
                    applicationLogger.d(message)
                }
            }
        }
    }
}

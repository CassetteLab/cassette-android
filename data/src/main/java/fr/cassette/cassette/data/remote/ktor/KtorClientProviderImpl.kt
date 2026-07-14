package fr.cassette.cassette.data.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

internal class KtorClientProviderImpl(
    private val applicationLogger: fr.cassette.cassette.core.logger.Logger
) : KtorClientProvider {

    private val defaultHeadersInterceptor = createClientPlugin("DefaultHeadersInterceptor") {
        onRequest { request, _ ->
            if (request.headers[HttpHeaders.Accept] == null) {
                request.headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }

    override fun getClient(): HttpClient = HttpClient(Android) {
        expectSuccess = true

        install(defaultHeadersInterceptor)
        install(Logging){
            logger = object: Logger {
                override fun log(message: String) {
                    applicationLogger.d(message)
                }
            }
        }
    }
}
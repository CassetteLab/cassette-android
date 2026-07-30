package fr.cassettelabs.cassette.data.remote.ktor.plugins

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.data.remote.ktor.KtorPluginProvider
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess


internal class LoggerPluginConfiguration {
    lateinit var logger: Logger
    private val sanitizeHeaderPredicates: MutableList<(String) -> Boolean> = mutableListOf()
    private val sanitizeParameterPredicates: MutableList<(String) -> Boolean> = mutableListOf()

    fun sanitizeHeader(predicate: (String) -> Boolean) {
        sanitizeHeaderPredicates.add(predicate)
    }

    fun sanitizeParameter(predicate: (String) -> Boolean) {
        sanitizeParameterPredicates.add(predicate)
    }

    internal fun isHeaderSanitized(header: String): Boolean = sanitizeHeaderPredicates.any { it(header) }

    internal fun isParameterSanitized(parameter: String): Boolean = sanitizeParameterPredicates.any { it(parameter) }
}

private const val SANITIZED_VALUE = "***"

private fun Map.Entry<String, List<String>>.sanitize(config: LoggerPluginConfiguration): String {
    val value = if (config.isHeaderSanitized(key)) SANITIZED_VALUE else value.joinToString()
    return "$key: $value"
}

private fun Url.sanitize(config: LoggerPluginConfiguration): String {
    val sanitizedUrl = URLBuilder(this)
    sanitizedUrl.sanitizeParameters(config)
    return sanitizedUrl.buildString()
}

private fun URLBuilder.sanitize(config: LoggerPluginConfiguration): String {
    val sanitizedUrl = URLBuilder(build())
    sanitizedUrl.sanitizeParameters(config)
    return sanitizedUrl.buildString()
}

private fun URLBuilder.sanitizeParameters(config: LoggerPluginConfiguration) {
    parameters.entries().forEach { (key, _) ->
        if (config.isParameterSanitized(key)) {
            parameters[key] = "xxx"
        }
    }
}

internal class LoggerPluginProvider : KtorPluginProvider<LoggerPluginConfiguration> {
    override fun getPlugin() = createClientPlugin("LoggerPlugin", ::LoggerPluginConfiguration) {
        val config = pluginConfig
        config.logger.init("Ktor Logger Plugin")

        onRequest { request, body ->
            val headers =
                request.headers.entries().joinToString("\n") { "\t${it.sanitize(config)}" }
            val isJson = request.headers[HttpHeaders.ContentType]?.contains("json") == true
            config.logger.d(
                buildString {
                    appendLine("REQUEST: ${request.url.sanitize(config)}")
                    appendLine("METHOD: ${request.method.value}")
                    appendLine("HEADERS:\n$headers")
                    if (isJson) append("BODY: $body")
                }
            )
        }
        onResponse { response ->
            val headers =
                response.headers.entries().joinToString("\n") { "\t${it.sanitize(config)}" }
            val isJson = response.contentType()?.match(ContentType.Application.Json) == true
            val body = if (isJson) response.bodyAsText() else null
            val message = buildString {
                appendLine("RESPONSE: ${response.status.value} ${response.status.description}")
                appendLine("METHOD: ${response.request.method.value}")
                appendLine("FROM: ${response.request.url.sanitize(config)}")
                appendLine("HEADERS:\n$headers")
                if (body != null) append("BODY: $body")
            }
            if (response.status.isSuccess()) {
                config.logger.d(message)
            } else {
                config.logger.w(message)
            }
        }
    }
}

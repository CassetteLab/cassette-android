package fr.cassette.cassette.data.remote.ktor.plugins

import fr.cassette.cassette.domain.models.ServerConfiguration
import io.ktor.client.plugins.api.createClientPlugin
import java.security.MessageDigest

internal class CassetteRequestAuthenticationPluginConfig {
    var serverConfigurationProvider: suspend () -> ServerConfiguration? = { null }
}

internal val cassetteRequestAuthenticationPlugin = createClientPlugin(
    name = "CassetteRequestAuthenticationPlugin",
    createConfiguration = ::CassetteRequestAuthenticationPluginConfig,
) {
    val serverConfigurationProvider = pluginConfig.serverConfigurationProvider

    onRequest { request, _ ->
        if (request.hasAuthenticationParameters()) return@onRequest

        val serverConfiguration = serverConfigurationProvider() ?: return@onRequest
        val salt = System.currentTimeMillis().toString(16)

        request.appendParameterIfAbsent("u", serverConfiguration.username)
        request.appendParameterIfAbsent("t", md5(serverConfiguration.password + salt))
        request.appendParameterIfAbsent("s", salt)

        serverConfiguration.customHeaders
            .filter { it.name.isNotBlank() }
            .forEach { customHeader ->
                request.appendHeaderIfAbsent(customHeader.name, customHeader.value)
            }
    }
}

private fun io.ktor.client.request.HttpRequestBuilder.appendParameterIfAbsent(name: String, value: String) {
    if (!url.parameters.contains(name)) {
        url.parameters.append(name, value)
    }
}

private fun io.ktor.client.request.HttpRequestBuilder.hasAuthenticationParameters(): Boolean {
    return url.parameters.contains("u") ||
        url.parameters.contains("t") ||
        url.parameters.contains("s")
}

private fun io.ktor.client.request.HttpRequestBuilder.appendHeaderIfAbsent(name: String, value: String) {
    if (headers[name] == null) {
        headers.append(name, value)
    }
}

private fun md5(value: String): String = MessageDigest.getInstance("MD5")
    .digest(value.toByteArray())
    .joinToString(separator = "") { byte -> "%02x".format(byte) }

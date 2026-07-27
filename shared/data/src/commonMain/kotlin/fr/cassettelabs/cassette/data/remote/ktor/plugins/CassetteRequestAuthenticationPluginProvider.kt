package fr.cassettelabs.cassette.data.remote.ktor.plugins

import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.ktor.KtorPluginProvider
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.data.remote.ktor.md5
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

internal class CassetteRequestAuthenticationPluginProvider(
    private val logger: Logger,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val cipherHelper: CipherHelper,
) : KtorPluginProvider<Unit> {
    override fun getPlugin(): ClientPlugin<Unit> =
        createClientPlugin(
            name = "CassetteRequestAuthenticationPlugin",
        ) {
            onRequest { request, _ ->
                if (request.hasAuthenticationParameters()) {
                    logger.w("Request already have authentication parameters")
                    return@onRequest
                }

                val configuration = serverConfigurationDao.getServerConfiguration()
                if (configuration == null) {
                    logger.w("Configuration is null can't add authentication parameters")
                    return@onRequest
                }

                val salt = currentTimeMillis().toString(16)
                val password = cipherHelper.decrypt(configuration.serverConfiguration.encryptedPassword)
                request.appendParameterIfAbsent("u", configuration.serverConfiguration.username)
                request.appendParameterIfAbsent("t", md5(password + salt))
                request.appendParameterIfAbsent("s", salt)

                configuration.customHeaders
                    .filter { it.name.isNotBlank() }
                    .forEach { customHeader ->
                        val value = cipherHelper.decrypt(customHeader.encryptedValue)
                        request.appendHeaderIfAbsent(customHeader.name, value)
                    }
            }
        }

    private fun io.ktor.client.request.HttpRequestBuilder.appendParameterIfAbsent(
        name: String,
        value: String,
    ) {
        if (!url.parameters.contains(name)) {
            url.parameters.append(name, value)
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.hasAuthenticationParameters(): Boolean =
        url.parameters.contains("u") ||
            url.parameters.contains("t") ||
            url.parameters.contains("s")

    private fun io.ktor.client.request.HttpRequestBuilder.appendHeaderIfAbsent(
        name: String,
        value: String,
    ) {
        if (headers[name] == null) {
            headers.append(name, value)
        }
    }
}

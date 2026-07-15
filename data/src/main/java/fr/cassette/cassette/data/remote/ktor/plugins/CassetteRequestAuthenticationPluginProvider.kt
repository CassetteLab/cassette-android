package fr.cassette.cassette.data.remote.ktor.plugins

import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.ktor.KtorPluginProvider
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import java.security.MessageDigest

internal class CassetteRequestAuthenticationPluginProvider(
    private val logger: Logger,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val cipherHelper: CipherHelper,
) : KtorPluginProvider<Unit> {
    override fun getPlugin(): ClientPlugin<Unit> = createClientPlugin(
        name = "CassetteRequestAuthenticationPlugin",
    ) {
        onRequest { request, _ ->
            if (request.hasAuthenticationParameters()) {
                logger.w("Request already have authentication parameters")
                return@onRequest
            }

            val configuration = serverConfigurationDao.getFirstServerConfiguration()
            if (configuration == null){
                logger.w("Configuration is null can't add authentication parameters")
                return@onRequest
            }

            val salt = System.currentTimeMillis().toString(16)
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
}
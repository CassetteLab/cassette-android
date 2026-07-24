package fr.cassettelabs.cassette.data.remote.ktor.plugins

import fr.cassettelabs.cassette.data.remote.ktor.KtorPluginProvider
import io.ktor.client.plugins.api.createClientPlugin

internal class CassetteRequestDefaultsPluginProvider : KtorPluginProvider<Unit> {
    override fun getPlugin() =
        createClientPlugin("CassetteRequestDefaultsPlugin") {
            onRequest { request, _ ->
                request.appendParameterIfAbsent("v", "1.16.1")
                request.appendParameterIfAbsent("c", "Cassette")
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
}

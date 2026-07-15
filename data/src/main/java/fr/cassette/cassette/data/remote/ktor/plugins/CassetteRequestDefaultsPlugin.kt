package fr.cassette.cassette.data.remote.ktor.plugins

import io.ktor.client.plugins.api.createClientPlugin

internal val cassetteRequestDefaultsPlugin = createClientPlugin("CassetteRequestDefaultsPlugin") {
    onRequest { request, _ ->
        request.appendParameterIfAbsent("v", "1.16.1")
        request.appendParameterIfAbsent("c", "Cassette")
        request.appendParameterIfAbsent("f", "json")
    }
}

private fun io.ktor.client.request.HttpRequestBuilder.appendParameterIfAbsent(name: String, value: String) {
    if (!url.parameters.contains(name)) {
        url.parameters.append(name, value)
    }
}

package fr.cassette.cassette.data.remote.ktor

import io.ktor.client.plugins.api.ClientPlugin

internal interface KtorPluginProvider<T : Any> {
    fun getPlugin(): ClientPlugin<T>
}

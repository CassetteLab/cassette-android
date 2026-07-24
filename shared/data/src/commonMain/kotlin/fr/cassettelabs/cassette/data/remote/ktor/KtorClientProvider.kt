package fr.cassettelabs.cassette.data.remote.ktor

import io.ktor.client.HttpClient

internal interface KtorClientProvider {
    fun getClient(): HttpClient
}

package fr.cassettelabs.cassette.data.remote.ktor

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.HttpClientEngineConfig

internal expect fun ktorClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig>

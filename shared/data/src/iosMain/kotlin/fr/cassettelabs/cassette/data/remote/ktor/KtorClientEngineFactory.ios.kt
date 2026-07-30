package fr.cassettelabs.cassette.data.remote.ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun ktorClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig> = Darwin

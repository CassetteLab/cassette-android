package fr.cassettelabs.cassette.data.remote.ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

internal actual fun ktorClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig> = Android

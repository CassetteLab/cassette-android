package fr.cassette.cassette.presentation.core.serverConfiguration.core

import java.net.URI

internal object ServerConfigurationValidator {
    private val headerNameRegex = Regex("^[!#$%&'*+.^_`|~0-9A-Za-z-]+$")

    fun isValidUrl(value: String): Boolean {
        val uri = runCatching { URI(value.trim()) }.getOrNull() ?: return false
        return uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
    }

    fun isHttpUrl(value: String): Boolean {
        val uri = runCatching { URI(value.trim()) }.getOrNull() ?: return false
        return uri.scheme == "http"
    }

    fun isValidHeaderName(value: String): Boolean = headerNameRegex.matches(value)

    fun isValidHeaderValue(value: String): Boolean = value.none { it == '\r' || it == '\n' || it == '\u0000' }
}
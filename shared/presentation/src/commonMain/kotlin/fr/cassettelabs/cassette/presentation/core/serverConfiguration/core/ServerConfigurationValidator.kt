package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

internal object ServerConfigurationValidator {
    private val headerNameRegex = Regex("^[!#$%&'*+.^_`|~0-9A-Za-z-]+$")
    private val urlRegex = Regex("^(https?)://([^/?#]+)(?:[/?#].*)?$")

    fun isValidUrl(value: String): Boolean {
        val match = urlRegex.matchEntire(value.trim()) ?: return false
        return match.groupValues[2].isNotBlank()
    }

    fun isHttpUrl(value: String): Boolean = value.trim().startsWith("http://")

    fun isValidHeaderName(value: String): Boolean = headerNameRegex.matches(value)

    fun isValidHeaderValue(value: String): Boolean = value.none { it == '\r' || it == '\n' || it == '\u0000' }
}

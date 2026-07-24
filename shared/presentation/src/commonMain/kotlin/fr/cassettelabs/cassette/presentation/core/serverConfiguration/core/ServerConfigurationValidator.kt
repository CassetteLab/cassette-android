package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

internal object ServerConfigurationValidator {
    private val headerNameRegex = Regex("^[!#${'$'}%&'*+.^_`|~0-9A-Za-z-]+${'$'}")
    private val urlRegex = Regex("^https?://[^\\s/$.?#].[^\\s]*${'$'}", RegexOption.IGNORE_CASE)

    fun isValidUrl(value: String): Boolean = urlRegex.matches(value.trim())

    fun isHttpUrl(value: String): Boolean = value.trim().startsWith("http://", ignoreCase = true)

    fun isValidHeaderName(value: String): Boolean = headerNameRegex.matches(value)

    fun isValidHeaderValue(value: String): Boolean = value.none { it == '\r' || it == '\n' || it == '\u0000' }
}

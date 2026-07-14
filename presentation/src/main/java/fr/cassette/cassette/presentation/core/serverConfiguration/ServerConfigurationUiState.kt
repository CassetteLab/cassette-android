package fr.cassette.cassette.presentation.core.serverConfiguration

import fr.cassette.cassette.presentation.core.serverConfiguration.core.ServerConfigurationValidator

internal data class ServerConfigurationUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val customHeaders: List<ServerConfigurationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
) {
    val isUrlValid: Boolean = serverUrl.isBlank() || ServerConfigurationValidator.isValidUrl(serverUrl)
    val isHttp: Boolean = ServerConfigurationValidator.isHttpUrl(serverUrl)
    val areHeadersValid: Boolean = customHeaders.all { it.isValid }
    val canSubmit: Boolean = serverUrl.isNotBlank() &&
        username.isNotBlank() &&
        password.isNotBlank() &&
        isUrlValid &&
        areHeadersValid &&
        !isLoading
}

internal data class ServerConfigurationHeaderUiState(
    val id: Long,
    val name: String = "",
    val value: String = "",
    val isValueVisible: Boolean = false,
) {
    val isNameValid: Boolean = name.isBlank() || ServerConfigurationValidator.isValidHeaderName(name)
    val isValueValid: Boolean = value.isBlank() || ServerConfigurationValidator.isValidHeaderValue(value)
    val isValid: Boolean = isNameValid && isValueValid
}

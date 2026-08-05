package fr.cassettelabs.cassette.presentation.settings

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class SettingsUiState(
    val versionName: String = "",
    val versionCode: String = "",
    val isDebugBuild: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
) : UiState

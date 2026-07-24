package fr.cassettelabs.cassette.presentation.settings

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class SettingsUiState(
    val isWifiOnlyDownloadsEnabled: Boolean = true,
    val areNotificationsEnabled: Boolean = true,
    val versionName: String = "",
    val versionCode: String = "",
    val isDebugBuild: Boolean = false,
) : UiState

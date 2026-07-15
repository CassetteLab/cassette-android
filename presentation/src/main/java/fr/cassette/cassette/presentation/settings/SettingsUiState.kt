package fr.cassette.cassette.presentation.settings

import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class SettingsUiState(
    val isWifiOnlyDownloadsEnabled: Boolean = true,
    val areNotificationsEnabled: Boolean = true,
) : UiState

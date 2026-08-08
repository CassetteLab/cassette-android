package fr.cassettelabs.cassette.presentation.settings.logs

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class SettingsLogsUiState(
    val isPlaceholderVisible: Boolean = true,
) : UiState

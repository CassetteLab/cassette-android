package fr.cassettelabs.cassette.presentation.settings.logs

import fr.cassettelabs.cassette.core.helpers.LogFileInfo
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class SettingsLogsUiState(
    val isLoading: Boolean = true,
    val isExporting: Boolean = false,
    val logFiles: List<LogFileInfo> = emptyList(),
    val exportFailed: Boolean = false,
) : UiState

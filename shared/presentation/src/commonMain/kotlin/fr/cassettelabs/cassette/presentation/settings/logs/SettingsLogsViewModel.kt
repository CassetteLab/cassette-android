package fr.cassettelabs.cassette.presentation.settings.logs

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.helpers.LogFilesHelper
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class SettingsLogsViewModel(
    private val logFilesHelper: LogFilesHelper,
    logger: Logger,
) : BaseViewModel<SettingsLogsUiState, SettingsLogsEvent>(
    viewModelName = "SettingsLogsViewModel",
    logger = logger,
    initialState = SettingsLogsUiState(),
) {
    override fun handleEvent(event: SettingsLogsEvent) {
        when (event) {
            SettingsLogsEvent.OnAppearing -> loadLogFiles()
            SettingsLogsEvent.OnBackClicked -> Unit
            SettingsLogsEvent.OnExportLogDirectoryClicked -> exportLogDirectory()
        }
    }

    private fun loadLogFiles() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, exportFailed = false) }
            try {
                val logFiles = logFilesHelper.getLogFiles()
                updateState {
                    it.copy(
                        isLoading = false,
                        logFiles = logFiles,
                    )
                }
            } catch (exception: Exception) {
                logger.w("Unable to load log files: ${exception.message}")
                updateState { it.copy(isLoading = false, logFiles = emptyList()) }
            }
        }
    }

    private fun exportLogDirectory() {
        if (uiState.value.isExporting) return

        viewModelScope.launch {
            updateState { it.copy(isExporting = true, exportFailed = false) }
            val exported =
                try {
                    logFilesHelper.exportLogDirectory()
                } catch (exception: Exception) {
                    logger.w("Unable to export log directory: ${exception.message}")
                    false
                }
            updateState { it.copy(isExporting = false, exportFailed = !exported) }
        }
    }
}

package fr.cassettelabs.cassette.presentation.settings.logs

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel

internal class SettingsLogsViewModel(
    logger: Logger,
) : BaseViewModel<SettingsLogsUiState, SettingsLogsEvent>(
    viewModelName = "SettingsLogsViewModel",
    logger = logger,
    initialState = SettingsLogsUiState(),
) {
    override fun handleEvent(event: SettingsLogsEvent) {
        when (event) {
            SettingsLogsEvent.OnBackClicked -> Unit
        }
    }
}

package fr.cassettelabs.cassette.presentation.settings

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel

internal class SettingsViewModel(
    logger: Logger,
) : BaseViewModel<SettingsUiState, SettingsEvent>(
        viewModelName = "SettingsViewModel",
        logger = logger,
        initialState = SettingsUiState(),
    ) {
    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnServerConfigurationClicked -> Unit
            is SettingsEvent.OnWifiOnlyDownloadsChanged ->
                updateState { uiState ->
                    uiState.copy(isWifiOnlyDownloadsEnabled = event.isEnabled)
                }
            is SettingsEvent.OnNotificationsChanged ->
                updateState { uiState ->
                    uiState.copy(areNotificationsEnabled = event.isEnabled)
                }
        }
    }
}

package fr.cassette.cassette.presentation.settings

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class SettingsViewModel(
    logger: Logger,
) : BaseViewModel<SettingsUiState, SettingsEvent>(
    viewModelName = "SettingsViewModel",
    logger = logger,
    initialState = SettingsUiState(),
) {
    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnBackClicked -> Unit
            SettingsEvent.OnServerConfigurationClicked -> Unit
            is SettingsEvent.OnWifiOnlyDownloadsChanged -> updateState { uiState ->
                uiState.copy(isWifiOnlyDownloadsEnabled = event.isEnabled)
            }
            is SettingsEvent.OnNotificationsChanged -> updateState { uiState ->
                uiState.copy(areNotificationsEnabled = event.isEnabled)
            }
        }
    }
}

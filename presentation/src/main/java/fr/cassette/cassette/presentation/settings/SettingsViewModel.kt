package fr.cassette.cassette.presentation.settings

import fr.cassette.cassette.core.helpers.ApplicationInformationHelper
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class SettingsViewModel(
    logger: Logger,
    applicationInformationHelper: ApplicationInformationHelper,
) : BaseViewModel<SettingsUiState, SettingsEvent>(
    viewModelName = "SettingsViewModel",
    logger = logger,
    initialState = SettingsUiState(
        versionName = applicationInformationHelper.versionName,
        versionCode = applicationInformationHelper.versionCode,
        isDebugBuild = applicationInformationHelper.isDebugBuild,
    ),
) {
    override fun handleEvent(event: SettingsEvent) {
        when (event) {
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

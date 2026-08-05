package fr.cassettelabs.cassette.presentation.settings

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.configuration.LogoutUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    logger: Logger,
    private val applicationInformationHelper: ApplicationInformationHelper,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<SettingsUiState, SettingsEvent>(
        viewModelName = "SettingsViewModel",
        logger = logger,
        initialState = SettingsUiState(),
    ) {
    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnLogoutClicked -> {
                if (uiState.value.isLoggingOut) return

                viewModelScope.launch {
                    updateState { it.copy(isLoggingOut = true) }
                    logoutUseCase()
                    updateState { it.copy(isLoggingOut = false, isLoggedOut = true) }
                }
            }
            SettingsEvent.OnAppearing -> {
                viewModelScope.launch {
                    updateState {
                        it.copy(
                            versionName = applicationInformationHelper.versionName,
                            versionCode = applicationInformationHelper.versionCode,
                            isDebugBuild = applicationInformationHelper.isDebugBuild
                        )
                    }
                }
            }
        }
    }
}

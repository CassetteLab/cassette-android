package fr.cassettelabs.cassette.presentation.settings.appearance

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.helpers.SettingsHelper
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class AppearanceSettingsViewModel(
    logger: Logger,
    private val settingsHelper: SettingsHelper,
) : BaseViewModel<AppearanceSettingsUiState, AppearanceSettingsEvent>(
        viewModelName = "AppearanceSettingsViewModel",
        logger = logger,
        initialState = AppearanceSettingsUiState(),
    ) {
    init {
        settingsHelper.themeFlow
            .onEach { themeMode ->
                updateState { it.copy(themeMode = themeMode) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: AppearanceSettingsEvent) {
        when (event) {
            is AppearanceSettingsEvent.OnThemeSelected -> {
                viewModelScope.launch {
                    settingsHelper.setTheme(event.themeMode)
                }
            }
            is AppearanceSettingsEvent.OnBackClicked -> Unit
        }
    }
}

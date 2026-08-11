package fr.cassettelabs.cassette.presentation.settings.appearance

import fr.cassettelabs.cassette.core.theme.ThemeMode
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class AppearanceSettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
) : UiState

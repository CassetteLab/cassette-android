package fr.cassettelabs.cassette.presentation.settings.appearance

import fr.cassettelabs.cassette.core.theme.ThemeMode
import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface AppearanceSettingsEvent : Event {
    data class OnThemeSelected(val themeMode: ThemeMode) : AppearanceSettingsEvent
    data object OnBackClicked : AppearanceSettingsEvent
}

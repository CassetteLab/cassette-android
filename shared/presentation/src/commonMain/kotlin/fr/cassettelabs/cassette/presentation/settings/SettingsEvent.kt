package fr.cassettelabs.cassette.presentation.settings

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface SettingsEvent : Event {
    data object OnAppearing: SettingsEvent
    data object OnLogoutClicked : SettingsEvent
}

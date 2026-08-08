package fr.cassettelabs.cassette.presentation.settings.logs

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface SettingsLogsEvent : Event {
    data object OnBackClicked : SettingsLogsEvent
}

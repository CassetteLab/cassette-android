package fr.cassettelabs.cassette.presentation.settings.logs

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface SettingsLogsEvent : Event {
    data object OnAppearing : SettingsLogsEvent

    data object OnBackClicked : SettingsLogsEvent

    data object OnExportLogDirectoryClicked : SettingsLogsEvent
}

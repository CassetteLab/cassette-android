package fr.cassettelabs.cassette.presentation.settings

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface SettingsEvent : Event {
    data object OnServerConfigurationClicked : SettingsEvent

    data class OnWifiOnlyDownloadsChanged(
        val isEnabled: Boolean,
    ) : SettingsEvent

    data class OnNotificationsChanged(
        val isEnabled: Boolean,
    ) : SettingsEvent
}

package fr.cassette.cassette.presentation.core.serverConfiguration

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface ServerConfigurationEvent : Event {
    data object OnBackClicked: ServerConfigurationEvent
    data class OnServerUrlChanged(val value: String) : ServerConfigurationEvent
    data class OnUsernameChanged(val value: String) : ServerConfigurationEvent
    data class OnPasswordChanged(val value: String) : ServerConfigurationEvent
    data object OnAddHeaderClicked : ServerConfigurationEvent
    data class OnRemoveHeaderClicked(val id: Long) : ServerConfigurationEvent
    data class OnHeaderNameChanged(val id: Long, val value: String) : ServerConfigurationEvent
    data class OnHeaderValueChanged(val id: Long, val value: String) : ServerConfigurationEvent
    data class OnHeaderValueVisibilityChanged(val id: Long, val isVisible: Boolean) : ServerConfigurationEvent
    data object OnConnectClicked : ServerConfigurationEvent
}

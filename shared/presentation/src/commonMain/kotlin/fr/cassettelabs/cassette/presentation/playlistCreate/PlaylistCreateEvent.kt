package fr.cassettelabs.cassette.presentation.playlistCreate

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistCreateEvent : Event {
    data object OnAppearing : PlaylistCreateEvent
    data object OnBackClicked : PlaylistCreateEvent
    data class OnNameChanged(val name: String) : PlaylistCreateEvent
    data class OnTrackToggled(val trackId: String) : PlaylistCreateEvent
    data object OnCreateClicked : PlaylistCreateEvent
}

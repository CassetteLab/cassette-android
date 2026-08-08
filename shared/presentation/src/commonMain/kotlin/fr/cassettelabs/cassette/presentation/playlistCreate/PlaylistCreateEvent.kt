package fr.cassettelabs.cassette.presentation.playlistCreate

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistCreateEvent : Event {
    data object OnBackClicked : PlaylistCreateEvent
}

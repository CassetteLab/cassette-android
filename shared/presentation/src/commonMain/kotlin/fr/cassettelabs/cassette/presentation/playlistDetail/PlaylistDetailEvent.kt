package fr.cassettelabs.cassette.presentation.playlistDetail

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistDetailEvent : Event {
    data object OnAppearing : PlaylistDetailEvent

    data object OnBackClicked : PlaylistDetailEvent

    data class OnTrackClicked(
        val trackId: String,
    ) : PlaylistDetailEvent
}

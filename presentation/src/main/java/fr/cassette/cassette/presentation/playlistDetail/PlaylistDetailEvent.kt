package fr.cassette.cassette.presentation.playlistDetail

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistDetailEvent : Event {
    data object OnAppearing : PlaylistDetailEvent

    data object OnBackClicked : PlaylistDetailEvent

    data class OnTrackClicked(
        val trackId: String,
    ) : PlaylistDetailEvent
}

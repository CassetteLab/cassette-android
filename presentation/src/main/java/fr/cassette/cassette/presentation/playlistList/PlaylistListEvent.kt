package fr.cassette.cassette.presentation.playlistList

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistListEvent : Event {
    data object OnAppearing : PlaylistListEvent

    data object OnRefresh : PlaylistListEvent

    data class OnPlaylistClicked(
        val playlistId: String,
    ) : PlaylistListEvent
}

package fr.cassettelabs.cassette.presentation.playlistList

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface PlaylistListEvent : Event {
    data object OnAppearing : PlaylistListEvent

    data object OnRefresh : PlaylistListEvent

    data object OnCreatePlaylistClicked : PlaylistListEvent

    data class OnPlaylistClicked(
        val playlistId: String,
    ) : PlaylistListEvent

    data class OnPlaylistCoverArtAppeared(
        val playlistId: String,
    ) : PlaylistListEvent
}

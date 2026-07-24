package fr.cassettelabs.cassette.presentation.albumList

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface AlbumListEvent : Event {
    data object OnAppearing : AlbumListEvent

    data object OnRefresh : AlbumListEvent


    data class OnAlbumClicked(
        val albumId: String,
    ) : AlbumListEvent
}

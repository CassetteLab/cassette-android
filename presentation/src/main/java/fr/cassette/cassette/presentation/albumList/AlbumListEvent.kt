package fr.cassette.cassette.presentation.albumList

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface AlbumListEvent : Event {
    data object OnAppearing : AlbumListEvent
    data object OnRefresh : AlbumListEvent
    data object OnRetryClicked : AlbumListEvent
    data class OnAlbumClicked(val albumId: String) : AlbumListEvent
}

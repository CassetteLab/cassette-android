package fr.cassette.cassette.presentation.albumDetail

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface AlbumDetailEvent : Event {
    data object OnBackClicked : AlbumDetailEvent
    data object OnRetryClicked : AlbumDetailEvent
}

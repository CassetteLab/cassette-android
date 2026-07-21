package fr.cassette.cassette.presentation.home

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface HomeEvent : Event {
    data object OnRetryClicked : HomeEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : HomeEvent
}

package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface HomeEvent : Event {
    data object OnRetryClicked : HomeEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : HomeEvent
}

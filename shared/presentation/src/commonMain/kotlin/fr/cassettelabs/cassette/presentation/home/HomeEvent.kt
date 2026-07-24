package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface HomeEvent : Event {
    data object OnAppearing : HomeEvent

    data object OnRefresh : HomeEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : HomeEvent
}

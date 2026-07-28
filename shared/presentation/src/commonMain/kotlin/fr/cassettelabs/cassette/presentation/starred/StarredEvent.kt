package fr.cassettelabs.cassette.presentation.starred

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface StarredEvent : Event {
    data object OnAppearing : StarredEvent

    data object OnRefresh : StarredEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : StarredEvent

    data class OnTrackClicked(
        val trackId: String,
    ) : StarredEvent
}

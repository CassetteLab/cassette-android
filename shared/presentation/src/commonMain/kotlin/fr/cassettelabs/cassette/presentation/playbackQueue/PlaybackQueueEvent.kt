package fr.cassettelabs.cassette.presentation.playbackQueue

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface PlaybackQueueEvent : Event {
    data object OnBackClicked : PlaybackQueueEvent
    data object OnAppearing : PlaybackQueueEvent

    data class OnTrackMoved(
        val fromIndex: Int,
        val toIndex: Int,
    ) : PlaybackQueueEvent
}

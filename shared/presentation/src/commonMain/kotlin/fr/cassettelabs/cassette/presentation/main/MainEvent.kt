package fr.cassettelabs.cassette.presentation.main

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface MainEvent : Event {
    data object OnAppearing : MainEvent

    data object OnPauseCurrentTrack : MainEvent

    data object OnPlayCurrentTrack : MainEvent

    data object OnNextTrack : MainEvent
}

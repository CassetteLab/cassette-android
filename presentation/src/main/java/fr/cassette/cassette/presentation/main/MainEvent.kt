package fr.cassette.cassette.presentation.main

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface MainEvent : Event {
    data object OnAppearing : MainEvent
}

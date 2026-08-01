package fr.cassettelabs.cassette.presentation.libraryPlaceholder

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface LibraryPlaceholderEvent : Event {
    data object OnBackClicked : LibraryPlaceholderEvent
}

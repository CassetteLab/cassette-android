package fr.cassette.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingCompleteEvent : Event {
    data object OnStartListeningClicked : OnBoardingCompleteEvent
}

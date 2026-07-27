package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingCompleteEvent : Event {
    data object OnStartListeningClicked : OnBoardingCompleteEvent
}

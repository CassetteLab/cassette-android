package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingWelcomeEvent : Event {
    data object OnAppearing : OnBoardingWelcomeEvent
    data object OnGetStartedClicked : OnBoardingWelcomeEvent
}

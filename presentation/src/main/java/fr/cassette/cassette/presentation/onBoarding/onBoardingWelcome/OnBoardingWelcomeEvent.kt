package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingWelcomeEvent : Event {
    data object OnAppearing : OnBoardingWelcomeEvent
    data object OnGetStartedClicked : OnBoardingWelcomeEvent
}

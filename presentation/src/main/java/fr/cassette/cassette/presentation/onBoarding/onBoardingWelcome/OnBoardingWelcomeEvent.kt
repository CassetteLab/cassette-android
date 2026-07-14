package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

internal sealed interface OnBoardingWelcomeEvent {
    data object OnAppearing : OnBoardingWelcomeEvent
    data object OnGetStartedClicked : OnBoardingWelcomeEvent
    data object OnServerConfigurationBackClicked : OnBoardingWelcomeEvent
}

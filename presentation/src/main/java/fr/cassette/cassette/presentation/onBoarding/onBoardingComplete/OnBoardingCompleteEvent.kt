package fr.cassette.cassette.presentation.onBoarding.onBoardingComplete

internal sealed interface OnBoardingCompleteEvent {
    data object OnStartListeningClicked : OnBoardingCompleteEvent
}

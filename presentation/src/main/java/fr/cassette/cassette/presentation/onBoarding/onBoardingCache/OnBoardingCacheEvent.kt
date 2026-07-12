package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

internal sealed interface OnBoardingCacheEvent {
    data object OnAppearing: OnBoardingCacheEvent
}
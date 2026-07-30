package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingCache

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingCacheEvent : Event {
    data object OnAppearing : OnBoardingCacheEvent
}

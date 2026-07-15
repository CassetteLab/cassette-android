package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

import fr.cassette.cassette.presentation.core.mvi.Event

internal sealed interface OnBoardingCacheEvent : Event {
    data object OnAppearing : OnBoardingCacheEvent
}

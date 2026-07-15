package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class OnBoardingCacheUiState(
    val maxTrackToCache: Int = 0,
    val isCacheOverCellularChecked: Boolean = false,
) : UiState

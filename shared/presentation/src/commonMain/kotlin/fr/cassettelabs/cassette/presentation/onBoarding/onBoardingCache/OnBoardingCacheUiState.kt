package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingCache

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class OnBoardingCacheUiState(
    val maxTrackToCache: Int = 0,
    val isCacheOverCellularChecked: Boolean = false,
) : UiState

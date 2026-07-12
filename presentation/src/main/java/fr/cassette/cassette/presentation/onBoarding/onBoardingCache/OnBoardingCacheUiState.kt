package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

internal data class OnBoardingCacheUiState(
    val maxTrackToCache: Int = 0,
    val isCacheOverCellularChecked: Boolean = false,
)
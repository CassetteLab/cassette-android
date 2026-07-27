package fr.cassettelabs.cassette.presentation.main

import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class MainUiState(
    val currentTrack: Track? = null,
    val coverArtFilePath: String? = null,
    val isPlaying: Boolean = false,
) : UiState

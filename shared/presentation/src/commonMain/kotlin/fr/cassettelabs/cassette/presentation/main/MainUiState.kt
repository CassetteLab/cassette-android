package fr.cassettelabs.cassette.presentation.main

import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class MainUiState(
    val currentTrack: CurrentTrack? = null,
    val coverArtFilePath: String? = null,
    val isPlaying: Boolean = false,
) : UiState

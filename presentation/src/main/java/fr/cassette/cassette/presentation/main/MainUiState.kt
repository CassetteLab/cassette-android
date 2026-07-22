package fr.cassette.cassette.presentation.main

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class MainUiState(
    val currentTrack: CurrentTrack? = null,
    val coverArtFilePath: String? = null,
    val isPlaying: Boolean = false,
) : UiState

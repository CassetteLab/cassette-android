package fr.cassette.cassette.presentation.main

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class MainUiState(
    val currentTrack: Track? = null,
) : UiState

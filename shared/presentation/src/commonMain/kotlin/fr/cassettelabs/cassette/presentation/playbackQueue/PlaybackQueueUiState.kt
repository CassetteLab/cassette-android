package fr.cassettelabs.cassette.presentation.playbackQueue

import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaybackQueueUiState(
    val upcomingTracks: List<CurrentTrack> = emptyList(),
) : UiState

package fr.cassettelabs.cassette.presentation.playbackQueue

import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaybackQueueUiState(
    val currentTrack: Track? = null,
    val upcomingTracks: List<Track> = emptyList(),
) : UiState

package fr.cassettelabs.cassette.presentation.nowPlaying

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class NowPlayingUiState(
    val trackId: String = "",
    val title: String = "",
    val artist: String? = null,
    val album: String? = null,
    val coverArtStatus: CoverArtLoadingStatus? = null,
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 0,
    val isPlaying: Boolean = true,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.Off,
    val isStarred: Boolean = false,
) : UiState {
    val progress: Float =
        if (durationSeconds > 0) {
            currentPositionSeconds.toFloat() / durationSeconds.toFloat()
        } else {
            0f
        }.coerceIn(0f, 1f)

    val currentPositionLabel: String = currentPositionSeconds.toDurationLabel()
    val durationLabel: String = durationSeconds.toDurationLabel()
}

private fun Int.toDurationLabel(): String = "${this / 60}:${(this % 60).toString().padStart(2, '0')}"

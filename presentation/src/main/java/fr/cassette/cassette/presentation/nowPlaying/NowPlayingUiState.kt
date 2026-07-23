package fr.cassette.cassette.presentation.nowPlaying

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.RepeatMode
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class NowPlayingUiState(
    val trackId: String = "",
    val title: String = "",
    val artist: String? = null,
    val album: String? = null,
    val coverArt: AlbumCoverArt? = null,
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 0,
    val isPlaying: Boolean = true,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.Off,
    val isFavorite: Boolean = false,
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

private fun Int.toDurationLabel(): String = "%d:%02d".format(this / 60, this % 60)

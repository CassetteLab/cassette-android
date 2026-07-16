package fr.cassette.cassette.presentation.nowPlaying

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class NowPlayingViewModel(
    trackId: String,
    logger: Logger,
) : BaseViewModel<NowPlayingUiState, NowPlayingEvent>(
    viewModelName = "NowPlayingViewModel",
    logger = logger,
    initialState = NowPlayingUiState(trackId = trackId),
) {
    override fun handleEvent(event: NowPlayingEvent) {
        when (event) {
            NowPlayingEvent.OnBackClicked -> Unit
            NowPlayingEvent.OnNextClicked -> updateState { it.copy(title = "Veridis Quo", artist = "Daft Punk", album = "Discovery", currentPositionSeconds = 24, durationSeconds = 345) }
            NowPlayingEvent.OnPlayPauseClicked -> updateState { it.copy(isPlaying = !it.isPlaying) }
            NowPlayingEvent.OnPreviousClicked -> updateState { it.copy(title = "One More Time", artist = "Daft Punk", album = "Discovery", currentPositionSeconds = 42, durationSeconds = 320) }
            NowPlayingEvent.OnShuffleClicked -> updateState { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
            NowPlayingEvent.OnRepeatClicked -> updateState { it.copy(repeatMode = it.repeatMode.next()) }
            NowPlayingEvent.OnFavoriteClicked -> updateState { it.copy(isFavorite = !it.isFavorite) }
            is NowPlayingEvent.OnSeekChanged -> updateState {
                it.copy(currentPositionSeconds = (it.durationSeconds * event.progress).toInt())
            }
        }
    }
}

private fun RepeatMode.next(): RepeatMode = when (this) {
    RepeatMode.Off -> RepeatMode.All
    RepeatMode.All -> RepeatMode.One
    RepeatMode.One -> RepeatMode.Off
}

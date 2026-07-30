package fr.cassettelabs.cassette.presentation.playbackQueue

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.GetPlaybackQueueUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.ReorderPlaybackQueueUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PlaybackQueueViewModel(
    private val getPlaybackQueueUseCase: GetPlaybackQueueUseCase,
    private val getCurrentTrackUseCase: GetCurrentTrackUseCase,
    private val reorderPlaybackQueueUseCase: ReorderPlaybackQueueUseCase,
    logger: Logger,
) : BaseViewModel<PlaybackQueueUiState, PlaybackQueueEvent>(
        viewModelName = "PlaybackQueueViewModel",
        logger = logger,
        initialState = PlaybackQueueUiState(),
    ) {

    override fun handleEvent(event: PlaybackQueueEvent) {
        when (event) {
            PlaybackQueueEvent.OnBackClicked -> Unit
            PlaybackQueueEvent.OnAppearing -> {
                getPlaybackQueueUseCase()
                    .onEach { queue -> updateState { it.copy(upcomingTracks = queue) } }
                    .launchIn(viewModelScope)

                getCurrentTrackUseCase()
                    .onEach { newCurrentTrack -> updateState { it.copy(currentTrack = newCurrentTrack) } }
                    .launchIn(viewModelScope)
            }
            is PlaybackQueueEvent.OnTrackMoved -> moveTrack(fromIndex = event.fromIndex, toIndex = event.toIndex)
        }
    }

    private fun moveTrack(
        fromIndex: Int,
        toIndex: Int,
    ) {
        val tracks = uiState.value.upcomingTracks
        if (fromIndex !in tracks.indices || toIndex !in tracks.indices) return
        if (fromIndex == toIndex) return

        updateState { state ->
            state.copy(upcomingTracks = state.upcomingTracks.move(fromIndex = fromIndex, toIndex = toIndex))
        }

        viewModelScope.launch {
            reorderPlaybackQueueUseCase(fromIndex = fromIndex, toIndex = toIndex)
        }
    }
}

private fun <T> List<T>.move(
    fromIndex: Int,
    toIndex: Int,
): List<T> =
    toMutableList().apply {
        add(toIndex, removeAt(fromIndex))
    }

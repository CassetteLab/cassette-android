package fr.cassettelabs.cassette.presentation.playbackQueue

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.GetPlaybackQueueUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PlaybackQueueViewModel(
    private val getPlaybackQueueUseCase: GetPlaybackQueueUseCase,
    private val getCurrentTrackUseCase: GetCurrentTrackUseCase,
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
        }
    }
}

package fr.cassettelabs.cassette.presentation.playbackQueue

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.GetPlaybackQueueUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PlaybackQueueViewModel(
    getPlaybackQueueUseCase: GetPlaybackQueueUseCase,
    logger: Logger,
) : BaseViewModel<PlaybackQueueUiState, PlaybackQueueEvent>(
        viewModelName = "PlaybackQueueViewModel",
        logger = logger,
        initialState = PlaybackQueueUiState(),
    ) {
    init {
        getPlaybackQueueUseCase()
            .onEach { queue -> updateState { it.copy(upcomingTracks = queue) } }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: PlaybackQueueEvent) {
        when (event) {
            PlaybackQueueEvent.OnBackClicked -> Unit
        }
    }
}

package fr.cassette.cassette.presentation.main

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassette.cassette.domain.usecases.GetPlaybackStateUseCase
import fr.cassette.cassette.domain.usecases.PausePlaybackUseCase
import fr.cassette.cassette.domain.usecases.PlayCurrentTrackUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class MainViewModel(
    logger: Logger,
    private val getCurrentTrackUseCase: GetCurrentTrackUseCase,
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val pausePlaybackUseCase: PausePlaybackUseCase,
    private val playCurrentTrackUseCase: PlayCurrentTrackUseCase,
) : BaseViewModel<MainUiState, MainEvent>(
        viewModelName = "MainViewModel",
        logger = logger,
        initialState = MainUiState(),
    ) {
    init {
        viewModelScope.launch {
            getCurrentTrackUseCase().collect { track ->
                updateState { it.copy(currentTrack = track) }
            }
        }

        getPlaybackStateUseCase()
            .onEach { playbackState ->
                updateState { it.copy(isPlaying = playbackState.isPlaying) }
            }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnAppearing -> Unit
            MainEvent.OnNextTrack -> Unit
            MainEvent.OnPauseCurrentTrack -> {
                pausePlaybackUseCase()
            }
            MainEvent.OnPlayCurrentTrack -> {
                playCurrentTrackUseCase()
            }
        }
    }
}

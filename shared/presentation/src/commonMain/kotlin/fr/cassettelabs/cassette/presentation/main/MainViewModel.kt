package fr.cassettelabs.cassette.presentation.main

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.usecases.album.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaybackStateUseCase
import fr.cassettelabs.cassette.domain.usecases.PausePlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.PlayCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.SkipToNextTrackUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class MainViewModel(
    logger: Logger,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val getCurrentTrackUseCase: GetCurrentTrackUseCase,
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val pausePlaybackUseCase: PausePlaybackUseCase,
    private val playCurrentTrackUseCase: PlayCurrentTrackUseCase,
    private val skipToNextTrackUseCase: SkipToNextTrackUseCase,
) : BaseViewModel<MainUiState, MainEvent>(
        viewModelName = "MainViewModel",
        logger = logger,
        initialState = MainUiState(),
    ) {
    init {
        viewModelScope.launch {
            getCurrentTrackUseCase().collect { track ->
                updateState {
                    it.copy(
                        currentTrack = track,
                        coverArtFilePath = track?.coverArtFilePath,
                    )
                }
                if (track != null && track.coverArtFilePath == null) {
                    loadCoverArt(track)
                }
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
            MainEvent.OnNextTrack -> viewModelScope.launch { skipToNextTrackUseCase() }
            MainEvent.OnPauseCurrentTrack -> {
                pausePlaybackUseCase()
            }
            MainEvent.OnPlayCurrentTrack -> {
                playCurrentTrackUseCase()
            }
        }
    }

    private fun loadCoverArt(currentTrack: CurrentTrack) {
        val coverArtId = currentTrack.coverArtId ?: return
        viewModelScope.launch {
            runCatching {
                getAlbumCoverArtUseCase(
                    coverArtId = coverArtId,
                    size = COVER_ART_SIZE,
                    albumId = currentTrack.albumId,
                )
            }.onSuccess { coverArt ->
                updateState { state ->
                    if (state.currentTrack?.track?.id == currentTrack.track.id) {
                        state.copy(coverArtFilePath = coverArt.filePath)
                    } else {
                        state
                    }
                }
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 160
    }
}

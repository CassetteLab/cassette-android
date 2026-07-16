package fr.cassette.cassette.presentation.nowPlaying

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class NowPlayingViewModel(
    trackId: String,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    getCurrentTrackUseCase: GetCurrentTrackUseCase,
    logger: Logger,
) : BaseViewModel<NowPlayingUiState, NowPlayingEvent>(
    viewModelName = "NowPlayingViewModel",
    logger = logger,
    initialState = NowPlayingUiState(trackId = trackId),
) {
    init {
        getCurrentTrackUseCase()
            .onEach { currentTrack ->
                if (currentTrack?.track?.id == uiState.value.trackId) {
                    updateCurrentTrack(currentTrack)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: NowPlayingEvent) {
        when (event) {
            NowPlayingEvent.OnBackClicked -> Unit
            NowPlayingEvent.OnNextClicked -> Unit
            NowPlayingEvent.OnPlayPauseClicked -> updateState { it.copy(isPlaying = !it.isPlaying) }
            NowPlayingEvent.OnPreviousClicked -> Unit
            NowPlayingEvent.OnShuffleClicked -> updateState { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
            NowPlayingEvent.OnRepeatClicked -> updateState { it.copy(repeatMode = it.repeatMode.next()) }
            NowPlayingEvent.OnFavoriteClicked -> updateState { it.copy(isFavorite = !it.isFavorite) }
            is NowPlayingEvent.OnSeekChanged -> updateState {
                it.copy(currentPositionSeconds = (it.durationSeconds * event.progress).toInt())
            }
        }
    }

    private fun updateCurrentTrack(currentTrack: CurrentTrack) {
        updateState {
            it.copy(
                title = currentTrack.track.title,
                artist = currentTrack.track.artist,
                album = currentTrack.albumName,
                durationSeconds = currentTrack.track.durationSeconds ?: 0,
                coverArt = null,
            )
        }

        val coverArtId = currentTrack.coverArtId ?: return
        viewModelScope.launch {
            runCatching {
                getAlbumCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE)
            }.onSuccess { coverArt ->
                updateState { it.copy(coverArt = coverArt) }
            }.onFailure { exception ->
                logger.w("Unable to load cover art $coverArtId", exception)
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 900
    }
}

private fun RepeatMode.next(): RepeatMode = when (this) {
    RepeatMode.Off -> RepeatMode.All
    RepeatMode.All -> RepeatMode.One
    RepeatMode.One -> RepeatMode.Off
}

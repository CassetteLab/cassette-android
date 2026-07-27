package fr.cassettelabs.cassette.presentation.nowPlaying

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.domain.usecases.album.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaybackStateUseCase
import fr.cassettelabs.cassette.domain.usecases.PausePlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.PlayCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.SeekPlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.SetPlaybackRepeatModeUseCase
import fr.cassettelabs.cassette.domain.usecases.SetPlaybackShuffleEnabledUseCase
import fr.cassettelabs.cassette.domain.usecases.SkipToNextTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.SkipToPreviousTrackUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class NowPlayingViewModel(
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val pausePlaybackUseCase: PausePlaybackUseCase,
    private val playCurrentTrackUseCase: PlayCurrentTrackUseCase,
    private val seekPlaybackUseCase: SeekPlaybackUseCase,
    private val setPlaybackRepeatModeUseCase: SetPlaybackRepeatModeUseCase,
    private val setPlaybackShuffleEnabledUseCase: SetPlaybackShuffleEnabledUseCase,
    private val skipToNextTrackUseCase: SkipToNextTrackUseCase,
    private val skipToPreviousTrackUseCase: SkipToPreviousTrackUseCase,
    getCurrentTrackUseCase: GetCurrentTrackUseCase,
    getPlaybackStateUseCase: GetPlaybackStateUseCase,
    logger: Logger,
) : BaseViewModel<NowPlayingUiState, NowPlayingEvent>(
        viewModelName = "NowPlayingViewModel",
        logger = logger,
        initialState = NowPlayingUiState(),
    ) {
    init {
        getCurrentTrackUseCase()
            .onEach { currentTrack ->
                if (currentTrack != null) {
                    updateCurrentTrack(currentTrack)
                } else {
                    updateState { NowPlayingUiState() }
                }
            }.launchIn(viewModelScope)

        getPlaybackStateUseCase()
            .onEach { playbackState ->
                updateState {
                    it.copy(
                        isPlaying = playbackState.isPlaying,
                        currentPositionSeconds = playbackState.positionMs.toSeconds(),
                        durationSeconds =
                            playbackState.durationMs
                                .toSeconds()
                                .takeIf { duration -> duration > 0 } ?: it.durationSeconds,
                        isShuffleEnabled = playbackState.isShuffleEnabled,
                        repeatMode = playbackState.repeatMode,
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: NowPlayingEvent) {
        when (event) {
            NowPlayingEvent.OnBackClicked -> Unit
            NowPlayingEvent.OnNextClicked -> viewModelScope.launch { skipToNextTrackUseCase() }
            NowPlayingEvent.OnPlayPauseClicked -> {
                if (uiState.value.isPlaying) {
                    pausePlaybackUseCase()
                } else {
                    playCurrentTrackUseCase()
                }
            }
            NowPlayingEvent.OnPreviousClicked -> viewModelScope.launch { skipToPreviousTrackUseCase() }
            NowPlayingEvent.OnShuffleClicked -> {
                val isEnabled = !uiState.value.isShuffleEnabled
                viewModelScope.launch { setPlaybackShuffleEnabledUseCase(isEnabled) }
            }
            NowPlayingEvent.OnRepeatClicked -> {
                val repeatMode = uiState.value.repeatMode.next()
                viewModelScope.launch { setPlaybackRepeatModeUseCase(repeatMode) }
            }
            NowPlayingEvent.OnFavoriteClicked -> updateState { it.copy(isFavorite = !it.isFavorite) }
            NowPlayingEvent.OnQueueClicked -> Unit
            is NowPlayingEvent.OnSeekChanged -> {
                val positionSeconds = (uiState.value.durationSeconds * event.progress).toInt()
                seekPlaybackUseCase(positionSeconds * MILLIS_PER_SECOND)
                updateState {
                    it.copy(currentPositionSeconds = positionSeconds)
                }
            }
        }
    }

    private fun updateCurrentTrack(currentTrack: CurrentTrack) {
        updateState {
            it.copy(
                trackId = currentTrack.track.id,
                title = currentTrack.track.title,
                artist = currentTrack.track.artist,
                album = currentTrack.albumName,
                durationSeconds = currentTrack.track.durationSeconds ?: 0,
                coverArt = currentTrack.coverArtFilePath?.let { filePath -> AlbumCoverArt(filePath = filePath) },
            )
        }

        if (currentTrack.coverArtFilePath != null) return

        val coverArtId = currentTrack.coverArtId ?: return
        val trackId = currentTrack.track.id
        viewModelScope.launch {
            runCatching {
                getAlbumCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE, albumId = currentTrack.albumId)
            }.onSuccess { coverArt ->
                updateState { state ->
                    if (state.trackId == trackId) {
                        state.copy(coverArt = coverArt)
                    } else {
                        state
                    }
                }
            }.onFailure { exception ->
                logger.w("Unable to load cover art $coverArtId" + ": " + exception.message)
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 900
        const val MILLIS_PER_SECOND = 1_000L
    }
}

private fun Long.toSeconds(): Int = (this / 1_000L).toInt()

private fun RepeatMode.next(): RepeatMode =
    when (this) {
        RepeatMode.Off -> RepeatMode.All
        RepeatMode.All -> RepeatMode.One
        RepeatMode.One -> RepeatMode.Off
    }

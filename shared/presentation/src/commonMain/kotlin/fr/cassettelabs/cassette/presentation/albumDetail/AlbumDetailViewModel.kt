package fr.cassettelabs.cassette.presentation.albumDetail

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackContextType
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.GetPlaybackStateUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PlayTrackUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class AlbumDetailViewModel(
    private val albumId: String,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getAlbumTracksUseCase: GetAlbumTracksUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    getCurrentTrackUseCase: GetCurrentTrackUseCase,
    getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    logger: Logger,
) : BaseViewModel<AlbumDetailUiState, AlbumDetailEvent>(
        viewModelName = "AlbumDetailViewModel",
        logger = logger,
        initialState = AlbumDetailUiState(albumId = albumId),
    ) {
    init {
        getCurrentTrackUseCase()
            .onEach { currentTrack ->
                updateState { it.copy(currentTrackId = currentTrack?.track?.id) }
            }.launchIn(viewModelScope)

        getPlaybackStateUseCase()
            .onEach { playbackState ->
                updateState { it.copy(isPlaying = playbackState.isPlaying) }
            }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: AlbumDetailEvent) {
        when (event) {
            AlbumDetailEvent.OnAppearing -> {
                loadAlbum()
                loadAlbumTracks()
            }
            AlbumDetailEvent.OnBackClicked -> Unit
            is AlbumDetailEvent.OnTrackClicked -> playTrack(event.trackId)
        }
    }

    private fun playTrack(trackId: String) {
        val album = uiState.value.album ?: return
        val contextTracks =
            uiState.value.tracks.map { track ->
                CurrentTrack(
                    track = track,
                    albumId = album.id,
                    albumName = album.name,
                    coverArtId = album.coverArt ?: album.id,
                    coverArtFilePath = album.coverArtFilePath,
                )
            }
        contextTracks
            .firstOrNull { it.track.id == trackId }
            ?.let { currentTrack ->
                viewModelScope.launch {
                    playTrackUseCase(
                        currentTrack = currentTrack,
                        contextTracks = contextTracks,
                        context = PlaybackContext(type = PlaybackContextType.Album, id = album.id),
                    )
                }
            }
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val album = getAlbumUseCase(albumId)
                val coverArt =
                    album.coverArtFilePath?.let { filePath -> AlbumCoverArt(filePath = filePath) } ?: runCatching {
                        getAlbumCoverArtUseCase(
                            coverArtId = album.coverArt ?: album.id,
                            size = COVER_ART_SIZE,
                            albumId = album.id,
                        )
                    }.getOrNull()
                updateState { it.copy(isLoading = false, album = album, coverArt = coverArt) }
            } catch (exception: Exception) {
                logger.w("Unable to load album $albumId" + ": " + exception.message)
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadAlbumTracks() {
        viewModelScope.launch {
            updateState { it.copy(isTracksLoading = true) }
            try {
                val tracks = getAlbumTracksUseCase(albumId)
                updateState { it.copy(isTracksLoading = false, tracks = tracks) }
            } catch (exception: Exception) {
                logger.w("Unable to load album tracks $albumId" + ": " + exception.message)
                updateState { it.copy(isTracksLoading = false) }
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 600
    }
}

package fr.cassettelabs.cassette.presentation.albumDetail

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackContextType
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.RefreshAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.RefreshAlbumUseCase
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
    private val refreshAlbumUseCase: RefreshAlbumUseCase,
    private val refreshAlbumTracksUseCase: RefreshAlbumTracksUseCase,
    private val getCurrentTrackUseCase: GetCurrentTrackUseCase,
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    logger: Logger,
) : BaseViewModel<AlbumDetailUiState, AlbumDetailEvent>(
    viewModelName = "AlbumDetailViewModel",
    logger = logger,
    initialState = AlbumDetailUiState(),
) {
    override fun handleEvent(event: AlbumDetailEvent) {
        when (event) {
            AlbumDetailEvent.OnBackClicked -> Unit
            is AlbumDetailEvent.OnArtistClicked -> Unit
            AlbumDetailEvent.OnAppearing -> {
                loadAlbum()
                loadAlbumTracks()

                getCurrentTrackUseCase()
                    .onEach { currentTrack ->
                        updateState { it.copy(currentTrackId = currentTrack?.id) }
                    }.launchIn(viewModelScope)

                getPlaybackStateUseCase()
                    .onEach { playbackState ->
                        updateState { it.copy(isPlaying = playbackState.isPlaying) }
                    }.launchIn(viewModelScope)
            }
            AlbumDetailEvent.OnRefresh -> refreshAlbumDetail()
            is AlbumDetailEvent.OnTrackClicked -> playTrack(event.trackId)
        }
    }

    private fun playTrack(trackId: String) {
        val album = uiState.value.album ?: return
        val contextTracks =
            uiState.value.tracks.map { track ->
                track.copy(
                    albumId = album.id,
                    albumName = album.name,
                    coverArt = album.coverArt ?: album.id,
                    coverArtFilePath = album.coverArtFilePath,
                )
            }
        contextTracks
            .firstOrNull { it.id == trackId }
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
                updateState { it.copy(isLoading = false, album = album) }
                album?.let { loadCoverArt(it) }
            } catch (exception: Exception) {
                logger.w("Unable to load album $albumId" + ": " + exception.message)
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun refreshAlbumDetail() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }

            try {
                val album = refreshAlbumUseCase(albumId)
                updateState { it.copy(album = album) }
                loadCoverArt(album)
            } catch (exception: Exception) {
                logger.w("Unable to refresh album $albumId" + ": " + exception.message)
            }

            try {
                val tracks = refreshAlbumTracksUseCase(albumId)
                updateState { it.copy(tracks = tracks) }
            } catch (exception: Exception) {
                logger.w("Unable to refresh album tracks $albumId" + ": " + exception.message)
            }
        }.invokeOnCompletion {
            updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
        }
    }

    private fun loadCoverArt(album: Album) {
        getAlbumCoverArtUseCase(
            coverArtId = album.coverArt ?: album.id,
            size = COVER_ART_SIZE,
            albumId = album.id,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load cover art for album ${album.id}" + ": " + status.throwable.message)
            }
            updateState { it.copy(coverArtStatus = status) }
        }.launchIn(viewModelScope)
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
        const val COVER_ART_SIZE = 900
    }
}

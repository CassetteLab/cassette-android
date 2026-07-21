package fr.cassette.cassette.presentation.albumDetail

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackContext
import fr.cassette.cassette.domain.models.PlaybackContextType
import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumTracksUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumUseCase
import fr.cassette.cassette.domain.usecases.PlayTrackUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class AlbumDetailViewModel(
    private val albumId: String,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getAlbumTracksUseCase: GetAlbumTracksUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    logger: Logger,
) : BaseViewModel<AlbumDetailUiState, AlbumDetailEvent>(
        viewModelName = "AlbumDetailViewModel",
        logger = logger,
        initialState = AlbumDetailUiState(albumId = albumId),
    ) {
    override fun handleEvent(event: AlbumDetailEvent) {
        when (event) {
            AlbumDetailEvent.OnBackClicked -> Unit
            AlbumDetailEvent.OnAppearing -> {
                loadAlbum()
                loadAlbumTracks()
            }
            AlbumDetailEvent.OnRetryClicked -> {
                loadAlbum()
                loadAlbumTracks()
            }
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
            updateState { it.copy(isLoading = true, hasError = false) }
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
                logger.w("Unable to load album $albumId", exception)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private fun loadAlbumTracks() {
        viewModelScope.launch {
            updateState { it.copy(isTracksLoading = true, hasTracksError = false) }
            try {
                val tracks = getAlbumTracksUseCase(albumId)
                updateState { it.copy(isTracksLoading = false, tracks = tracks) }
            } catch (exception: Exception) {
                logger.w("Unable to load album tracks $albumId", exception)
                updateState { it.copy(isTracksLoading = false, hasTracksError = true) }
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 600
    }
}

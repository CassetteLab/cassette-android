package fr.cassettelabs.cassette.presentation.starred

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PlayTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.starred.GetStarredLibraryUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class StarredViewModel(
    private val getStarredLibraryUseCase: GetStarredLibraryUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    logger: Logger,
) : BaseViewModel<StarredUiState, StarredEvent>(
        viewModelName = "StarredViewModel",
        logger = logger,
        initialState = StarredUiState(),
    ) {
    override fun handleEvent(event: StarredEvent) {
        when (event) {
            StarredEvent.OnAppearing -> loadStarredLibrary(showLoading = true)
            StarredEvent.OnRefresh -> loadStarredLibrary(showLoading = false, showPullToRefresh = true)
            is StarredEvent.OnAlbumClicked -> Unit
            is StarredEvent.OnTrackClicked -> playTrack(event.trackId)
        }
    }

    private fun loadStarredLibrary(
        showLoading: Boolean,
        showPullToRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = showLoading,
                    isRefreshing = !showLoading,
                    isPullToRefreshIndicatorVisible = showPullToRefresh,
                )
            }

            try {
                val starredLibrary = getStarredLibraryUseCase()
                updateState {
                    it.copy(
                        isLoading = false,
                        albums = starredLibrary.albums,
                        tracks = starredLibrary.tracks,
                    )
                }
                downloadMissingCoverArts()
            } catch (exception: Exception) {
                logger.w("Unable to load starred library" + ": " + exception.message)
                updateState { it.copy(isLoading = false) }
            }

            updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
        }
    }

    private fun playTrack(trackId: String) {
        val contextTracks = uiState.value.tracks
        contextTracks
            .firstOrNull { it.id == trackId }
            ?.let { currentTrack ->
                viewModelScope.launch {
                    playTrackUseCase(currentTrack = currentTrack, contextTracks = contextTracks)
                }
            }
    }

    private suspend fun downloadMissingCoverArts() {
        val albums = uiState.value.albums
        albums.forEach { album ->
            val coverArtId = album.coverArt ?: return@forEach
            val coverArt =
                runCatching {
                    getAlbumCoverArtUseCase(
                        coverArtId = coverArtId,
                        size = ALBUM_COVER_ART_SIZE,
                        albumId = album.id,
                    )
                }.getOrNull() ?: return@forEach

            updateState { state ->
                state.copy(
                    albums =
                        state.albums.map { stateAlbum ->
                            if (stateAlbum.id == album.id) stateAlbum.copy(coverArtFilePath = coverArt.filePath) else stateAlbum
                        },
                )
            }
        }

        val tracks = uiState.value.tracks
        tracks.forEach { track ->
            val coverArtId = track.coverArt ?: return@forEach
            val coverArt =
                runCatching {
                    getAlbumCoverArtUseCase(
                        coverArtId = coverArtId,
                        size = TRACK_COVER_ART_SIZE,
                        albumId = track.albumId,
                    )
                }.getOrNull() ?: return@forEach

            updateTrackCoverArt(track = track, coverArtFilePath = coverArt.filePath)
        }
    }

    private fun updateTrackCoverArt(
        track: Track,
        coverArtFilePath: String,
    ) {
        updateState { state ->
            state.copy(
                tracks =
                    state.tracks.map { stateTrack ->
                        if (stateTrack.id == track.id) stateTrack.copy(coverArtFilePath = coverArtFilePath) else stateTrack
                    },
            )
        }
    }

    private companion object {
        const val ALBUM_COVER_ART_SIZE = 240
        const val TRACK_COVER_ART_SIZE = 160
    }
}

package fr.cassettelabs.cassette.presentation.albumList

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class AlbumListViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<AlbumListUiState, AlbumListEvent>(
    viewModelName = "AlbumListViewModel",
    logger = logger,
    initialState = AlbumListUiState(),
) {

    private var observingAlbumsJob : Job? = null
    private var refreshingAlbumsJob : Job? = null

    override fun handleEvent(event: AlbumListEvent) {
        when (event) {
            is AlbumListEvent.OnAlbumClicked -> Unit
            AlbumListEvent.OnAppearing -> {
                observingAlbumsJob?.cancel()
                observingAlbumsJob = viewModelScope.launch {
                    getAllAlbumsUseCase()
                        .onStart { updateState { it.copy(isLoading = true) } }
                        .collect { albums ->
                            updateState { it.copy(isLoading = false, albums = albums) }
                            downloadMissingAlbumCoverArts(albums)
                        }
                }

                if (refreshingAlbumsJob?.isActive?.not() ?: true){
                    refreshingAlbumsJob = viewModelScope.launch {
                        updateState { it.copy(isRefreshing = true) }
                        refreshAlbums()
                    }
                    refreshingAlbumsJob?.invokeOnCompletion {
                        updateState { it.copy(isRefreshing = false) }
                    }
                }
            }
            AlbumListEvent.OnRefresh -> {
                if (refreshingAlbumsJob?.isActive ?: true){
                    logger.w("refreshingAlbumsJob is active, can't refresh albums")
                    return
                }

                refreshingAlbumsJob = viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }
                    refreshAlbums()
                }
                refreshingAlbumsJob?.invokeOnCompletion {
                    updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
                }
            }
        }
    }

    private suspend fun refreshAlbums() {
        try {
            refreshAlbumsUseCase()
        } catch (exception: Exception) {
            logger.w("Unable to refresh albums" + ": " + exception.message)
        }
    }

    private fun downloadMissingAlbumCoverArts(albums: List<Album>) {
        albums.forEach { album ->
            val coverArtId = album.coverArt ?: album.id
            getAlbumCoverArtUseCase(
                coverArtId = coverArtId,
                size = COVER_ART_SIZE,
                albumId = album.id,
            ).onEach { status ->
                if (status is CoverArtLoadingStatus.Error) {
                    logger.w("Unable to load cover art for album ${album.id}" + ": " + status.throwable.message)
                }
                updateState { state ->
                    state.copy(albumCoverArtStatuses = state.albumCoverArtStatuses + (album.id to status))
                }
            }.launchIn(viewModelScope)
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 900
    }
}

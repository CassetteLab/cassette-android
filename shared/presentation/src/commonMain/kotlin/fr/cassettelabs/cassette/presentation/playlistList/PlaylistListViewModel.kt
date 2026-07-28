package fr.cassettelabs.cassette.presentation.playlistList

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.GetAllPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.RefreshPlaylistsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class PlaylistListViewModel(
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val refreshPlaylistsUseCase: RefreshPlaylistsUseCase,
    private val getPlaylistCoverArtUseCase: GetPlaylistCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistListUiState, PlaylistListEvent>(
    viewModelName = "PlaylistListViewModel",
    logger = logger,
    initialState = PlaylistListUiState(),
) {

    private var observingPlaylistsJob: Job? = null
    private var refreshingPlaylistsJob: Job? = null

    override fun handleEvent(event: PlaylistListEvent) {
        when (event) {
            PlaylistListEvent.OnAppearing -> {
                observingPlaylistsJob?.cancel()
                observingPlaylistsJob = viewModelScope.launch {
                    getAllPlaylistsUseCase()
                        .onStart { updateState { it.copy(isLoading = true) } }
                        .collect { playlists ->
                            updateState { it.copy(isLoading = false, playlists = playlists) }
                            downloadMissingPlaylistCoverArts(playlists)
                        }
                }

                if (refreshingPlaylistsJob?.isActive?.not() ?: true) {
                    refreshingPlaylistsJob = viewModelScope.launch {
                        updateState { it.copy(isRefreshing = true) }
                        refreshPlaylists()
                    }
                    refreshingPlaylistsJob?.invokeOnCompletion {
                        updateState { it.copy(isRefreshing = false) }
                    }
                }
            }
            is PlaylistListEvent.OnPlaylistClicked -> Unit
            PlaylistListEvent.OnRefresh -> {
                if (refreshingPlaylistsJob?.isActive == true) {
                    logger.w("refreshingPlaylistsJob is active, can't refresh playlists")
                    return
                }

                refreshingPlaylistsJob = viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }
                    refreshPlaylists()
                }
                refreshingPlaylistsJob?.invokeOnCompletion {
                    updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
                }
            }
        }
    }

    private suspend fun refreshPlaylists() {
        try {
            refreshPlaylistsUseCase()
        } catch (exception: Exception) {
            logger.w("Unable to refresh playlists" + ": " + exception.message)
        }
    }

    private fun downloadMissingPlaylistCoverArts(playlists: List<Playlist>) {
        playlists.forEach { playlist ->
            val coverArtId = playlist.coverArt ?: playlist.id
            getPlaylistCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE, playlistId = playlist.id)
                .onEach { status ->
                    if (status is CoverArtLoadingStatus.Error) {
                        logger.w("Unable to load cover art for playlist ${playlist.id}" + ": " + status.throwable.message)
                    }
                    updateState { state ->
                        state.copy(playlistCoverArtStatuses = state.playlistCoverArtStatuses + (playlist.id to status))
                    }
                }.launchIn(viewModelScope)
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 900
    }
}

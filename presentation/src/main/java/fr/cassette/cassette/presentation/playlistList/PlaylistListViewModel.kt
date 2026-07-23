package fr.cassette.cassette.presentation.playlistList

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.domain.usecases.GetAllPlaylistsUseCase
import fr.cassette.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassette.cassette.domain.usecases.RefreshPlaylistsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
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
    override fun handleEvent(event: PlaylistListEvent) {
        when (event) {
            PlaylistListEvent.OnAppearing -> {
                viewModelScope.launch {
                    getAllPlaylistsUseCase()
                        .onStart { updateState { it.copy(isLoading = true) } }
                        .collect { playlists ->
                            updateState { it.copy(isLoading = false, playlists = playlists) }
                            downloadMissingPlaylistCoverArts(playlists)
                        }
                }
                viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true) }
                    refreshPlaylists()
                }.invokeOnCompletion {
                    updateState { it.copy(isRefreshing = false) }
                }
            }
            is PlaylistListEvent.OnPlaylistClicked -> Unit
            PlaylistListEvent.OnRefresh -> {
                viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }
                    refreshPlaylists()
                }.invokeOnCompletion {
                    updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
                }
            }
        }
    }

    private suspend fun refreshPlaylists() {
        try {
            refreshPlaylistsUseCase()
        } catch (exception: Exception) {
            logger.w("Unable to refresh playlists", exception)
        }
    }

    private suspend fun downloadMissingPlaylistCoverArts(playlists: List<PlaylistList>) {
        playlists.forEach { playlist ->
            if (playlist.coverArtFilePath != null) return@forEach

            val coverArtId = playlist.coverArt ?: playlist.id
            runCatching {
                getPlaylistCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE, playlistId = playlist.id)
            }.onFailure { exception ->
                logger.w("Unable to load cover art for playlist ${playlist.id}", exception)
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 320
    }
}

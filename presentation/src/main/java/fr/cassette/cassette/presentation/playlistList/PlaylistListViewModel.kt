package fr.cassette.cassette.presentation.playlistList

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetAllPlaylistsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class PlaylistListViewModel(
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistListUiState, PlaylistListEvent>(
    viewModelName = "PlaylistListViewModel",
    logger = logger,
    initialState = PlaylistListUiState(),
) {
    override fun handleEvent(event: PlaylistListEvent) {
        when (event) {
            PlaylistListEvent.OnAppearing -> loadPlaylists()
            is PlaylistListEvent.OnPlaylistClicked -> Unit
            PlaylistListEvent.OnRetryClicked -> loadPlaylists()
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            getAllPlaylistsUseCase()
                .onStart { updateState { it.copy(isLoading = true, hasError = false) } }
                .catch { exception ->
                    logger.w("Unable to load playlists", exception)
                    updateState { it.copy(isLoading = false, hasError = true) }
                }
                .collect { playlists ->
                    updateState { it.copy(isLoading = false, playlists = playlists) }
                }
        }
    }
}

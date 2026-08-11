package fr.cassettelabs.cassette.presentation.playlistCreate

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.playlistCreate.CreatePlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistCreate.GetAllTracksUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class PlaylistCreateViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getAllTracksUseCase: GetAllTracksUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistCreateUiState, PlaylistCreateEvent>(
    viewModelName = "PlaylistCreateViewModel",
    logger = logger,
    initialState = PlaylistCreateUiState(),
) {
    override fun handleEvent(event: PlaylistCreateEvent) {
        when (event) {
            PlaylistCreateEvent.OnAppearing -> {
                loadTracks()
            }
            PlaylistCreateEvent.OnBackClicked -> Unit
            is PlaylistCreateEvent.OnNameChanged -> {
                updateState { it.copy(name = event.name) }
            }
            is PlaylistCreateEvent.OnTrackToggled -> {
                val currentIds = uiState.value.selectedTrackIds
                val newIds = if (event.trackId in currentIds) {
                    currentIds - event.trackId
                } else {
                    currentIds + event.trackId
                }
                updateState { it.copy(selectedTrackIds = newIds) }
            }
            PlaylistCreateEvent.OnCreateClicked -> {
                createPlaylist()
            }
        }
    }

    private fun loadTracks() {
        if (uiState.value.tracks.isNotEmpty()) return

        viewModelScope.launch {
            updateState { it.copy(isLoadingTracks = true) }
            try {
                val tracks = getAllTracksUseCase()
                updateState { it.copy(isLoadingTracks = false, tracks = tracks) }
            } catch (e: Exception) {
                logger.w("Unable to load tracks: ${e.message}")
                updateState { it.copy(isLoadingTracks = false) }
            }
        }
    }

    private fun createPlaylist() {
        val state = uiState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val createdPlaylist = createPlaylistUseCase(state.name, state.selectedTrackIds.toList())
                updateState { it.copy(isLoading = false, createdPlaylist = createdPlaylist) }
            } catch (e: Exception) {
                logger.w("Unable to create playlist: ${e.message}")
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}

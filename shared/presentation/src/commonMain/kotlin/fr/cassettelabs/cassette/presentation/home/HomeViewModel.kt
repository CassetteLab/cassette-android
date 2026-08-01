package fr.cassettelabs.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.GetAllPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.RefreshPlaylistsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val refreshPlaylistsUseCase: RefreshPlaylistsUseCase,
    private val getPlaylistCoverArtUseCase: GetPlaylistCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<HomeUiState, HomeEvent>(
        viewModelName = "HomeViewModel",
        logger = logger,
        initialState = HomeUiState(),
    ) {
    private val coverArtJobs = mutableMapOf<String, Job>()

    init {
        observeAlbums()
        observePlaylists()
        refreshLibraryPreviews()
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAlbumCoverArtAppeared -> downloadAlbumCoverArtIfNeeded(event.albumId)
            is HomeEvent.OnPlaylistCoverArtAppeared -> downloadPlaylistCoverArtIfNeeded(event.playlistId)
            else -> Unit
        }
    }

    private fun observeAlbums() {
        getAllAlbumsUseCase()
            .onEach { albums ->
                updateState { state -> state.copy(albums = albums.take(HOME_PREVIEW_SIZE)) }
            }.launchIn(viewModelScope)
    }

    private fun observePlaylists() {
        getAllPlaylistsUseCase()
            .onEach { playlists ->
                updateState { state -> state.copy(playlists = playlists.take(HOME_PREVIEW_SIZE)) }
            }.launchIn(viewModelScope)
    }

    private fun refreshLibraryPreviews() {
        viewModelScope.launch {
            try {
                refreshAlbumsUseCase()
            } catch (exception: Exception) {
                logger.w("Unable to refresh home albums" + ": " + exception.message)
            }

            try {
                refreshPlaylistsUseCase()
            } catch (exception: Exception) {
                logger.w("Unable to refresh home playlists" + ": " + exception.message)
            }
        }
    }

    private fun downloadAlbumCoverArtIfNeeded(albumId: String) {
        val state = uiState.value
        val album = state.albums.firstOrNull { it.id == albumId } ?: return
        val currentStatus = state.albumCoverArtStatuses[albumId]

        if (album.coverArtFilePath != null || currentStatus != null || coverArtJobs[albumId]?.isActive == true) return

        val coverArtId = album.coverArt ?: album.id
        coverArtJobs[albumId] = getAlbumCoverArtUseCase(
            coverArtId = coverArtId,
            size = COVER_ART_SIZE,
            albumId = album.id,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load home album cover art ${album.id}" + ": " + status.throwable.message)
            }
            updateState { currentState ->
                currentState.copy(albumCoverArtStatuses = currentState.albumCoverArtStatuses + (album.id to status))
            }
        }.launchIn(viewModelScope).also { job ->
            job.invokeOnCompletion { coverArtJobs.remove(albumId) }
        }
    }

    private fun downloadPlaylistCoverArtIfNeeded(playlistId: String) {
        val state = uiState.value
        val playlist = state.playlists.firstOrNull { it.id == playlistId } ?: return
        val currentStatus = state.playlistCoverArtStatuses[playlistId]

        if (playlist.coverArtFilePath != null || currentStatus != null || coverArtJobs[playlistId]?.isActive == true) return

        val coverArtId = playlist.coverArt ?: playlist.id
        coverArtJobs[playlistId] = getPlaylistCoverArtUseCase(
            coverArtId = coverArtId,
            size = COVER_ART_SIZE,
            playlistId = playlist.id,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load home playlist cover art ${playlist.id}" + ": " + status.throwable.message)
            }
            updateState { currentState ->
                currentState.copy(playlistCoverArtStatuses = currentState.playlistCoverArtStatuses + (playlist.id to status))
            }
        }.launchIn(viewModelScope).also { job ->
            job.invokeOnCompletion { coverArtJobs.remove(playlistId) }
        }
    }

    private companion object {
        const val HOME_PREVIEW_SIZE = 10
        const val COVER_ART_SIZE = 900
    }
}

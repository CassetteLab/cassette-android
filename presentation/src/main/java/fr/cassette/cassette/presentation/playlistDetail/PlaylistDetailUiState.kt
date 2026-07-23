package fr.cassette.cassette.presentation.playlistDetail

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.PlaylistDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class PlaylistDetailUiState(
    val playlistId: String,
    val playlist: PlaylistDetail? = null,
    val tracks: List<Track> = emptyList(),
    val coverArt: AlbumCoverArt? = null,
    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,
) : UiState

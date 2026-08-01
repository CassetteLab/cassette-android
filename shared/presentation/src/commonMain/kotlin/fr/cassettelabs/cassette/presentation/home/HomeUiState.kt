package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class HomeUiState(
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val albumCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
    val playlistCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
) : UiState

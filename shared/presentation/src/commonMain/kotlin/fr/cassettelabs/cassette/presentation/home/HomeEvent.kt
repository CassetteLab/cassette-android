package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface HomeEvent : Event {
    data object OnArtistsClicked : HomeEvent

    data object OnAlbumsClicked : HomeEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : HomeEvent

    data class OnAlbumCoverArtAppeared(
        val albumId: String,
    ) : HomeEvent

    data object OnTracksClicked : HomeEvent

    data object OnPlaylistsClicked : HomeEvent

    data class OnPlaylistClicked(
        val playlistId: String,
    ) : HomeEvent

    data class OnPlaylistCoverArtAppeared(
        val playlistId: String,
    ) : HomeEvent

    data object OnStarredClicked : HomeEvent

    data object OnDownloadsClicked : HomeEvent

    data object OnQueueClicked : HomeEvent
}

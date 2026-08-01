package fr.cassettelabs.cassette.presentation.nowPlaying

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface NowPlayingEvent : Event {
    data object OnBackClicked : NowPlayingEvent

    data object OnPlayPauseClicked : NowPlayingEvent

    data object OnPreviousClicked : NowPlayingEvent

    data object OnNextClicked : NowPlayingEvent

    data object OnShuffleClicked : NowPlayingEvent

    data object OnRepeatClicked : NowPlayingEvent

    data object OnStarredClicked : NowPlayingEvent

    data object OnQueueClicked : NowPlayingEvent

    data class OnArtistClicked(
        val artistId: String,
    ) : NowPlayingEvent

    data class OnSeekChanged(
        val progress: Float,
    ) : NowPlayingEvent
}

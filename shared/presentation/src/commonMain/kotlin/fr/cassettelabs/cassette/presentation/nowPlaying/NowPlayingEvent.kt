package fr.cassettelabs.cassette.presentation.nowPlaying

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface NowPlayingEvent : Event {
    data object OnBackClicked : NowPlayingEvent

    data object OnPlayPauseClicked : NowPlayingEvent

    data object OnPreviousClicked : NowPlayingEvent

    data object OnNextClicked : NowPlayingEvent

    data object OnShuffleClicked : NowPlayingEvent

    data object OnRepeatClicked : NowPlayingEvent

    data object OnFavoriteClicked : NowPlayingEvent

    data class OnSeekChanged(
        val progress: Float,
    ) : NowPlayingEvent
}

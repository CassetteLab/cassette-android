package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    val currentTrack: Flow<Track?>
    val playbackState: Flow<PlaybackState>
    val playbackQueue: Flow<List<Track>>

    suspend fun play(
        currentTrack: Track,
        contextTracks: List<Track> = emptyList(),
        context: PlaybackContext? = null,
    )

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)

    suspend fun skipToNext()

    suspend fun skipToPrevious()

    suspend fun setShuffleEnabled(isEnabled: Boolean)

    suspend fun setRepeatMode(repeatMode: RepeatMode)

    suspend fun reorderPlaybackQueue(
        fromIndex: Int,
        toIndex: Int,
    )
}

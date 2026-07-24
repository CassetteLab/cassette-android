package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackState
import fr.cassettelabs.cassette.domain.models.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    val currentTrack: Flow<CurrentTrack?>
    val playbackState: Flow<PlaybackState>

    suspend fun play(
        currentTrack: CurrentTrack,
        contextTracks: List<CurrentTrack> = emptyList(),
        context: PlaybackContext? = null,
    )

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)

    suspend fun skipToNext()

    suspend fun skipToPrevious()

    suspend fun setShuffleEnabled(isEnabled: Boolean)

    suspend fun setRepeatMode(repeatMode: RepeatMode)
}

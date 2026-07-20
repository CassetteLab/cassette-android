package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackState
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    val currentTrack: Flow<CurrentTrack?>
    val playbackState: Flow<PlaybackState>

    suspend fun play(currentTrack: CurrentTrack)

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)
}

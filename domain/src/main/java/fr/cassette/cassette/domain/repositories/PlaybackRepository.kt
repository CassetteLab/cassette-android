package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    val currentTrack: Flow<Track?>

    fun setCurrentTrack(track: Track)
}

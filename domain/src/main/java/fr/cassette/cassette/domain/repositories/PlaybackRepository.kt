package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.CurrentTrack
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    val currentTrack: Flow<CurrentTrack?>

    fun setCurrentTrack(currentTrack: CurrentTrack)
}

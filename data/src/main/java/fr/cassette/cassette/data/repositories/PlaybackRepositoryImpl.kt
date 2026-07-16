package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaybackRepositoryImpl : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack

    override fun setCurrentTrack(track: Track) {
        _currentTrack.value = track
    }
}

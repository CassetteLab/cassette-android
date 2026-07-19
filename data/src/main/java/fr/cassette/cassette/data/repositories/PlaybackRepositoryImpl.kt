package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaybackRepositoryImpl : PlaybackRepository {
    private val _currentTrack = MutableStateFlow<CurrentTrack?>(null)
    override val currentTrack: StateFlow<CurrentTrack?> = _currentTrack

    override fun setCurrentTrack(currentTrack: CurrentTrack) {
        _currentTrack.value = currentTrack
    }
}

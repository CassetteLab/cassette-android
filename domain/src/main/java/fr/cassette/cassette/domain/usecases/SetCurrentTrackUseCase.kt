package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.repositories.PlaybackRepository

class SetCurrentTrackUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    operator fun invoke(track: Track) {
        playbackRepository.setCurrentTrack(track)
    }
}

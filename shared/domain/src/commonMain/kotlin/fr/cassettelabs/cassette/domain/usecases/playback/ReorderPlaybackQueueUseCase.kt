package fr.cassettelabs.cassette.domain.usecases.playback

import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository

class ReorderPlaybackQueueUseCase(
    private val playbackRepository: PlaybackRepository,
) {
    suspend operator fun invoke(
        fromIndex: Int,
        toIndex: Int,
    ) {
        playbackRepository.reorderPlaybackQueue(fromIndex = fromIndex, toIndex = toIndex)
    }
}

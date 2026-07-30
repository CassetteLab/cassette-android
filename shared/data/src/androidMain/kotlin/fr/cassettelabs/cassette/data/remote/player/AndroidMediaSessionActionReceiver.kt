package fr.cassettelabs.cassette.data.remote.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class AndroidMediaSessionActionReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val playbackRepository = GlobalContext.getOrNull()?.get<PlaybackRepository>() ?: return
        when (intent.action) {
            ACTION_PLAY -> playbackRepository.play()
            ACTION_PAUSE -> playbackRepository.pause()
            ACTION_NEXT -> scope.launch { playbackRepository.skipToNext() }
            ACTION_PREVIOUS -> scope.launch { playbackRepository.skipToPrevious() }
        }
    }

    private companion object {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }
}

const val ACTION_PLAY = "fr.cassettelabs.cassette.action.PLAY"
const val ACTION_PAUSE = "fr.cassettelabs.cassette.action.PAUSE"
const val ACTION_NEXT = "fr.cassettelabs.cassette.action.NEXT"
const val ACTION_PREVIOUS = "fr.cassettelabs.cassette.action.PREVIOUS"

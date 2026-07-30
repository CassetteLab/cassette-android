package fr.cassettelabs.cassette.data.remote.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import fr.cassettelabs.cassette.domain.models.PlaybackState as CassettePlaybackState
import fr.cassettelabs.cassette.domain.models.Track

class AndroidMediaSessionController(private val context: Context) : PlatformMediaSessionController {
    private var callbacks: PlatformMediaSessionCallbacks? = null
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val mediaSession =
        MediaSession(context, MEDIA_SESSION_TAG).apply {
            setCallback(
                object : MediaSession.Callback() {
                    override fun onPlay() {
                        callbacks?.play()
                    }

                    override fun onPause() {
                        callbacks?.pause()
                    }

                    override fun onSeekTo(pos: Long) {
                        callbacks?.seekTo(pos)
                    }

                    override fun onSkipToNext() {
                        callbacks?.skipToNext()
                    }

                    override fun onSkipToPrevious() {
                        callbacks?.skipToPrevious()
                    }
                },
            )
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
        }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }
    }

    override fun setCallbacks(callbacks: PlatformMediaSessionCallbacks) {
        this.callbacks = callbacks
    }

    override fun update(
        track: Track?,
        playbackState: CassettePlaybackState,
    ) {
        mediaSession.setMetadata(track?.toMediaMetadata())
        mediaSession.setPlaybackState(playbackState.toAndroidPlaybackState())
        mediaSession.isActive = track != null
        if (track == null) {
            notificationManager.cancel(NOTIFICATION_ID)
        } else if (canPostNotifications()) {
            notificationManager.notify(NOTIFICATION_ID, track.toNotification(playbackState))
        }
    }

    override fun dispose() {
        notificationManager.cancel(NOTIFICATION_ID)
        mediaSession.isActive = false
        mediaSession.release()
    }

    private fun Track.toMediaMetadata(): MediaMetadata {
        val artwork = coverArtFilePath?.let(BitmapFactory::decodeFile)
        return MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, artist.orEmpty())
            .putString(MediaMetadata.METADATA_KEY_ALBUM, albumName.orEmpty())
            .putLong(MediaMetadata.METADATA_KEY_DURATION, durationSeconds?.toLong()?.times(1_000L) ?: 0L)
            .apply {
                artwork?.let {
                    putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, it)
                    putBitmap(MediaMetadata.METADATA_KEY_ART, it)
                }
            }
            .build()
    }

    private fun Track.toNotification(playbackState: CassettePlaybackState): Notification {
        val isPlaying = playbackState.isPlaying
        val playPauseAction = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            } else {
                Notification.Builder(context)
            }

        return builder
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(listOfNotNull(artist, albumName).joinToString(" - "))
            .setLargeIcon(coverArtFilePath?.let(::decodeNotificationArtwork))
            .setContentIntent(launchPendingIntent())
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setOngoing(isPlaying)
            .addAction(Notification.Action.Builder(android.R.drawable.ic_media_previous, "Previous", actionPendingIntent(ACTION_PREVIOUS)).build())
            .addAction(
                Notification.Action.Builder(
                    if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                    if (isPlaying) "Pause" else "Play",
                    actionPendingIntent(playPauseAction),
                ).build(),
            )
            .addAction(Notification.Action.Builder(android.R.drawable.ic_media_next, "Next", actionPendingIntent(ACTION_NEXT)).build())
            .setStyle(
                Notification.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2),
            )
            .build()
    }

    private fun launchPendingIntent(): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun actionPendingIntent(action: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            Intent(context, AndroidMediaSessionActionReceiver::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun decodeNotificationArtwork(filePath: String): Bitmap? =
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, this)
            inSampleSize = maxOf(1, maxOf(outWidth, outHeight) / NOTIFICATION_ARTWORK_SIZE_PX)
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(filePath, this)
        }

    private fun CassettePlaybackState.toAndroidPlaybackState(): PlaybackState {
        val state = if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        return PlaybackState.Builder()
            .setActions(PLAYBACK_ACTIONS)
            .setState(state, positionMs, if (isPlaying) 1.0f else 0.0f)
            .setExtras(Bundle())
            .build()
    }

    private companion object {
        const val MEDIA_SESSION_TAG = "Cassette"
        const val NOTIFICATION_CHANNEL_ID = "cassette_playback"
        const val NOTIFICATION_CHANNEL_NAME = "Playback"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_ARTWORK_SIZE_PX = 512
        const val PLAYBACK_ACTIONS =
            PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SEEK_TO or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS
    }
}

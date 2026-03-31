package com.example.navdemo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.navdemo.MainActivity
import com.example.navdemo.R

/**
 * A [MediaBrowserServiceCompat] that hosts a [MediaSessionCompat].
 *
 * This service:
 * - Creates and manages the Android MediaSession lifecycle.
 * - Publishes playback state and track metadata.
 * - Handles transport controls (play / pause / skip) via callbacks.
 * - Posts a foreground notification so the session stays alive while driving.
 *
 * Extend [SessionCallback] to integrate real audio playback or voice commands.
 */
class MediaPlaybackService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    // ── Fake playlist ────────────────────────────────────────────────────────
    private data class Track(val title: String, val artist: String, val album: String, val durationMs: Long)

    private val playlist = listOf(
        Track("Midnight Drive",   "Nova Synth",   "Neon Roads",    214_000L),
        Track("Horizon Protocol", "Cyber Drift",  "Velocity",      187_000L),
        Track("Pulse Zero",       "Elara X",      "Digital Karma", 231_000L),
        Track("Ultralight",       "Void Circuit", "Substratum",    198_000L)
    )
    private var currentTrackIndex = 0
    private var isPlaying = false
    private var positionMs = 0L

    // ── Service lifecycle ─────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaSession()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }

    // ── MediaBrowserServiceCompat ─────────────────────────────────────────────

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot(ROOT_ID, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(emptyList())
    }

    // ── MediaSession init ─────────────────────────────────────────────────────

    private fun initMediaSession() {
        val activityIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )

        mediaSession = MediaSessionCompat(this, TAG).apply {
            setSessionActivity(activityIntent)
            setCallback(SessionCallback())
            setPlaybackState(stateBuilder.setState(
                PlaybackStateCompat.STATE_PAUSED, positionMs, 1f
            ).build())
            isActive = true
        }

        sessionToken = mediaSession.sessionToken
        publishMetadata()
    }

    // ── Transport control callbacks ───────────────────────────────────────────

    inner class SessionCallback : MediaSessionCompat.Callback() {

        override fun onPlay() {
            isPlaying = true
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            startForeground(NOTIFICATION_ID, buildNotification())
        }

        override fun onPause() {
            isPlaying = false
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            stopForeground(STOP_FOREGROUND_DETACH)
        }

        override fun onSkipToNext() {
            currentTrackIndex = (currentTrackIndex + 1) % playlist.size
            positionMs = 0L
            publishMetadata()
            if (isPlaying) updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onSkipToPrevious() {
            currentTrackIndex = (currentTrackIndex - 1 + playlist.size) % playlist.size
            positionMs = 0L
            publishMetadata()
            if (isPlaying) updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onStop() {
            isPlaying = false
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun publishMetadata() {
        val track = playlist[currentTrackIndex]
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,  track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,  track.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.durationMs)
                .build()
        )
    }

    private fun updatePlaybackState(@PlaybackStateCompat.State state: Int) {
        mediaSession.setPlaybackState(
            stateBuilder.setState(state, positionMs, if (isPlaying) 1f else 0f).build()
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "NovaDrive Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Music playback controls"
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val track = playlist[currentTrackIndex]
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
            .build()
    }

    companion object {
        private const val TAG           = "MediaPlaybackService"
        private const val ROOT_ID       = "nova_drive_root"
        private const val CHANNEL_ID    = "nova_drive_playback"
        private const val NOTIFICATION_ID = 1001
    }
}

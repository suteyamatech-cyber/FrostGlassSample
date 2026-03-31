package com.example.navdemo.viewmodel

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navdemo.data.model.MediaState
import com.example.navdemo.service.MediaPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Manages media-playback state and coordinates with [MediaPlaybackService]
 * via [MediaBrowserCompat] / [MediaControllerCompat].
 *
 * The fake playlist cycles through predefined tracks for demo purposes.
 */
class MediaViewModel : ViewModel() {

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState: StateFlow<MediaState> = _mediaState.asStateFlow()

    // Fake playlist for demo
    private val playlist = listOf(
        MediaState(trackTitle = "Midnight Drive",   artistName = "Nova Synth",   albumName = "Neon Roads",    durationSec = 214),
        MediaState(trackTitle = "Horizon Protocol", artistName = "Cyber Drift",  albumName = "Velocity",      durationSec = 187),
        MediaState(trackTitle = "Pulse Zero",       artistName = "Elara X",      albumName = "Digital Karma", durationSec = 231),
        MediaState(trackTitle = "Ultralight",       artistName = "Void Circuit", albumName = "Substratum",    durationSec = 198)
    )
    private var playlistIndex = 0

    private var mediaBrowser: MediaBrowserCompat? = null
    private var controller: MediaControllerCompat? = null

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            val playing = state?.state == PlaybackStateCompat.STATE_PLAYING
            _mediaState.update { it.copy(isPlaying = playing) }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata ?: return
            _mediaState.update {
                it.copy(
                    trackTitle  = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: it.trackTitle,
                    artistName  = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: it.artistName,
                    albumName   = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: it.albumName,
                    durationSec = (metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) / 1000).toInt()
                )
            }
        }
    }

    /** Call once from the Activity / Composable with a Context to connect to the service. */
    fun connect(context: Context) {
        val component = ComponentName(context, MediaPlaybackService::class.java)
        mediaBrowser = MediaBrowserCompat(
            context,
            component,
            object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    val token = mediaBrowser?.sessionToken ?: return
                    controller = MediaControllerCompat(context, token).also { ctrl ->
                        ctrl.registerCallback(controllerCallback)
                        // Sync initial state
                        controllerCallback.onPlaybackStateChanged(ctrl.playbackState)
                        controllerCallback.onMetadataChanged(ctrl.metadata)
                    }
                }
            },
            null
        )
        mediaBrowser?.connect()

        // Also drive the fake progress bar independently
        startProgressTicker()
    }

    fun disconnect() {
        controller?.unregisterCallback(controllerCallback)
        mediaBrowser?.disconnect()
    }

    fun playPause() {
        val ctrl = controller
        if (ctrl != null) {
            if (_mediaState.value.isPlaying) ctrl.transportControls.pause()
            else ctrl.transportControls.play()
        } else {
            // Fallback when no service is bound (pure Compose demo)
            _mediaState.update { it.copy(isPlaying = !it.isPlaying) }
        }
    }

    fun skipNext() {
        playlistIndex = (playlistIndex + 1) % playlist.size
        val next = playlist[playlistIndex]
        _mediaState.update {
            it.copy(
                trackTitle  = next.trackTitle,
                artistName  = next.artistName,
                albumName   = next.albumName,
                durationSec = next.durationSec,
                progress    = 0f
            )
        }
        controller?.transportControls?.skipToNext()
    }

    fun skipPrevious() {
        playlistIndex = (playlistIndex - 1 + playlist.size) % playlist.size
        val prev = playlist[playlistIndex]
        _mediaState.update {
            it.copy(
                trackTitle  = prev.trackTitle,
                artistName  = prev.artistName,
                albumName   = prev.albumName,
                durationSec = prev.durationSec,
                progress    = 0f
            )
        }
        controller?.transportControls?.skipToPrevious()
    }

    /** Ticks the fake progress bar every second while playing. */
    private fun startProgressTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                val s = _mediaState.value
                if (s.isPlaying && s.durationSec > 0) {
                    val newProgress = (s.progress + 1f / s.durationSec).coerceAtMost(1f)
                    if (newProgress >= 1f) skipNext()
                    else _mediaState.update { it.copy(progress = newProgress) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}

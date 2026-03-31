package com.example.navdemo.data.model

/**
 * Snapshot of the media-playback state exposed to the UI layer.
 */
data class MediaState(
    val isPlaying: Boolean = false,
    val trackTitle: String = "Midnight Drive",
    val artistName: String = "Nova Synth",
    val albumName: String = "Neon Roads",
    /** Progress within the current track, 0.0–1.0 */
    val progress: Float = 0f,
    /** Track duration in seconds */
    val durationSec: Int = 210
)

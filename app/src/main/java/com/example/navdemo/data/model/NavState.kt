package com.example.navdemo.data.model

/**
 * Snapshot of the navigation state exposed to the UI layer.
 */
data class NavState(
    /** Current position index on the fake route. */
    val routeIndex: Int = 0,
    /** Current latitude. */
    val latitude: Double = 35.6896,
    /** Current longitude. */
    val longitude: Double = 139.6917,
    /** Camera bearing (degrees). */
    val bearing: Float = 0f,
    /** Current speed (km/h). */
    val speedKmh: Int = 0,
    /** Next instruction to display. */
    val nextInstruction: String = "Calculating route…",
    /** Distance remaining to the next waypoint (m). */
    val distanceToNext: Int = 0,
    /** Estimated time to destination (formatted string). */
    val eta: String = "--:--",
    /** Whether the demo route is actively animating. */
    val isDemoRunning: Boolean = false
)

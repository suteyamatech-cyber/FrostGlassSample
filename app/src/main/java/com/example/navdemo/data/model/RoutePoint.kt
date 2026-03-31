package com.example.navdemo.data.model

/**
 * Represents a geographic coordinate on the fake demo route.
 */
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    /** Compass bearing toward the next waypoint (degrees, 0 = north). */
    val bearing: Float = 0f,
    /** Speed the vehicle is assumed to be travelling at this point (km/h). */
    val speed: Int = 50,
    /** Human-readable instruction shown in the AI bar. */
    val instruction: String = ""
)

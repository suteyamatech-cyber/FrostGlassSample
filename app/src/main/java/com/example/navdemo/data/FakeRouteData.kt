package com.example.navdemo.data

import com.example.navdemo.data.model.RoutePoint

/**
 * A fake demo route through central Tokyo.
 * Coordinates roughly follow: Shinjuku → Harajuku → Shibuya → Ebisu → Daikanyama.
 * Each point carries the bearing toward the next waypoint and a navigation instruction.
 */
object FakeRouteData {

    val route: List<RoutePoint> = listOf(
        RoutePoint(35.6896, 139.6917,  90f, 40, "Head east on Meiji-dori"),
        RoutePoint(35.6896, 139.6950, 100f, 45, "Continue east – 1.2 km"),
        RoutePoint(35.6880, 139.6985, 140f, 50, "Bear right onto Omotesando"),
        RoutePoint(35.6850, 139.7010, 160f, 55, "Continue south on Omotesando"),
        RoutePoint(35.6810, 139.7030, 170f, 60, "Approaching Omotesando Hills"),
        RoutePoint(35.6770, 139.7040, 175f, 55, "Keep right toward Shibuya"),
        RoutePoint(35.6730, 139.7040, 185f, 50, "Turn left onto Koen-dori"),
        RoutePoint(35.6700, 139.7030, 200f, 45, "Cross Shibuya scramble crossing"),
        RoutePoint(35.6680, 139.7010, 220f, 40, "Turn right on Dogenzaka"),
        RoutePoint(35.6660, 139.6980, 240f, 45, "Head southwest toward Ebisu"),
        RoutePoint(35.6640, 139.6950, 250f, 50, "Continue on Yamate-dori"),
        RoutePoint(35.6620, 139.6930, 240f, 55, "Bear left at the overpass"),
        RoutePoint(35.6600, 139.6910, 230f, 50, "Approaching Daikanyama"),
        RoutePoint(35.6580, 139.6900, 225f, 40, "Turn right onto Log Road"),
        RoutePoint(35.6565, 139.6920, 150f, 35, "Destination ahead on the left"),
        RoutePoint(35.6550, 139.6920,   0f,  0, "You have arrived at your destination")
    )

    /** Pre-built list of AI assistant messages that cycle during navigation. */
    val aiMessages: List<String> = listOf(
        "Route optimised. Avoiding traffic on Shinjuku Expressway.",
        "Speed limit ahead: 40 km/h. Slow down.",
        "In 200 m, bear right onto Omotesando.",
        "Heavy traffic detected. Alternate route available.",
        "Approaching Shibuya crossing. Use the left lane.",
        "Weather update: light rain expected in 15 min.",
        "Driver monitoring: stay alert. 2 h 15 min remaining.",
        "Fuel efficiency: excellent. Range 340 km.",
        "Arriving at destination in approximately 12 minutes.",
        "Road work detected 500 m ahead. Speed reduced."
    )
}

package com.example.navdemo

import android.app.Application
import org.maplibre.android.MapLibre

/**
 * Application subclass used to initialise MapLibre once at startup.
 *
 * MapLibre's [MapLibre.getInstance] call must happen before any [MapView]
 * is created, making the Application class the ideal place for it.
 */
class NovaDriveApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // MapLibre initialisation – no API key required for the open-source SDK
        MapLibre.getInstance(this)
    }
}

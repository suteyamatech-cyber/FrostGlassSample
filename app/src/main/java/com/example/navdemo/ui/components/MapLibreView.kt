package com.example.navdemo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.navdemo.data.model.NavState
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

/**
 * MapLibre map wrapped in an [AndroidView] Composable.
 *
 * Features:
 * - No API key required – uses MapLibre demo vector tiles.
 * - 3D-like camera tilt (pitch ~50°) for the futuristic in-vehicle look.
 * - Smooth camera animation each time [navState] changes position.
 * - Full lifecycle management so the [MapView] is never leaked.
 *
 * To swap the tile source, change [MAP_STYLE_URL] to any MapLibre-compatible
 * style JSON URL (e.g. an OpenFreeMap or self-hosted Tileserver GL style).
 */
@Composable
fun MapLibreMapView(
    navState: NavState,
    modifier: Modifier = Modifier
) {
    val context     = LocalContext.current
    val lifecycle   = LocalLifecycleOwner.current.lifecycle

    // MapLibre is already initialised in NovaDriveApp.onCreate(); no init here.
    val mapView = remember {
        MapView(context).apply {
            getMapAsync { map ->
                map.setStyle(MAP_STYLE_URL)
                map.uiSettings.isCompassEnabled          = false
                map.uiSettings.isAttributionEnabled      = true
                map.uiSettings.isLogoEnabled             = false

                // Initial 3D camera position
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(navState.latitude, navState.longitude))
                    .zoom(DEFAULT_ZOOM)
                    .tilt(DEFAULT_TILT)
                    .bearing(navState.bearing.toDouble())
                    .build()
            }
        }
    }

    // Bind MapView to the hosting Activity / Fragment lifecycle.
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START   -> mapView.onStart()
                Lifecycle.Event.ON_RESUME  -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE   -> mapView.onPause()
                Lifecycle.Event.ON_STOP    -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else                       -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    // Animate camera whenever the nav-state position or bearing changes.
    LaunchedEffect(navState.latitude, navState.longitude, navState.bearing) {
        mapView.getMapAsync { map ->
            val newCamera = CameraPosition.Builder()
                .target(LatLng(navState.latitude, navState.longitude))
                .zoom(DEFAULT_ZOOM)
                .tilt(DEFAULT_TILT)
                .bearing(navState.bearing.toDouble())
                .build()
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(newCamera),
                CAMERA_ANIM_MS
            )
        }
    }

    AndroidView(
        factory  = { mapView },
        modifier = modifier
    )
}

// ── Constants ─────────────────────────────────────────────────────────────────

/** MapLibre public demo style – no API key required. */
private const val MAP_STYLE_URL  = "https://demotiles.maplibre.org/style.json"
private const val DEFAULT_ZOOM   = 15.5
private const val DEFAULT_TILT   = 50.0   // degrees – creates the 3D perspective
private const val CAMERA_ANIM_MS = 1_800  // smooth transition between waypoints

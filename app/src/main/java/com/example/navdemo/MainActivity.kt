package com.example.navdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navdemo.ui.screen.NavScreen
import com.example.navdemo.ui.theme.DarkBackground
import com.example.navdemo.ui.theme.NovaDriveTheme
import com.example.navdemo.viewmodel.MediaViewModel

/**
 * Single-Activity entry point for NovaDrive.
 *
 * Responsibilities:
 * 1. Enable edge-to-edge display (full-screen immersive for in-vehicle use).
 * 2. Apply the [NovaDriveTheme] dark neon-blue colour scheme.
 * 3. Connect the [MediaViewModel] to the [MediaPlaybackService] so that
 *    MediaSession integration is active for the lifetime of the Activity.
 * 4. Render [NavScreen] as the sole Composable destination.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NovaDriveTheme {
                val mediaViewModel: MediaViewModel = viewModel()

                // Connect once and disconnect when the composition leaves.
                // DisposableEffect(Unit) ensures this runs only once per composition,
                // regardless of how many times the UI recomposes.
                DisposableEffect(Unit) {
                    mediaViewModel.connect(this@MainActivity)
                    onDispose { mediaViewModel.disconnect() }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = DarkBackground
                ) {
                    NavScreen(mediaViewModel = mediaViewModel)
                }
            }
        }
    }
}

package com.example.navdemo.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navdemo.ui.components.AiAssistantBar
import com.example.navdemo.ui.components.MapLibreMapView
import com.example.navdemo.ui.components.MediaCard
import com.example.navdemo.ui.components.SpeedHud
import com.example.navdemo.ui.theme.AccentAmber
import com.example.navdemo.ui.theme.AccentGreen
import com.example.navdemo.ui.theme.DarkBackground
import com.example.navdemo.ui.theme.GlassBorder
import com.example.navdemo.ui.theme.GlassDark
import com.example.navdemo.ui.theme.NeonBlue
import com.example.navdemo.ui.theme.NeonBlueGlow
import com.example.navdemo.ui.theme.TextPrimary
import com.example.navdemo.ui.theme.TextSecondary
import com.example.navdemo.viewmodel.MediaViewModel
import com.example.navdemo.viewmodel.NavViewModel

/**
 * Primary full-screen navigation screen.
 *
 * Layout (top → bottom):
 *  ┌───────────────────────────────────────────────┐
 *  │  AiAssistantBar  (frosted-glass top overlay)  │
 *  ├───────────────────────────────────────────────┤
 *  │                                               │
 *  │         MapLibreMapView (full-screen)         │
 *  │         – 3D tilted camera (50° pitch)        │
 *  │         – animates along fake route           │
 *  │                                               │
 *  │  SpeedHud (bottom-start, over map)            │
 *  ├───────────────────────────────────────────────┤
 *  │  MediaCard  (frosted-glass bottom overlay)    │
 *  └───────────────────────────────────────────────┘
 *
 * A "Start Demo" / "Stop Demo" FAB in the bottom-end corner controls the
 * fake route playback.
 */
@Composable
fun NavScreen(
    navViewModel:   NavViewModel   = viewModel(),
    mediaViewModel: MediaViewModel = viewModel()
) {
    val navState   by navViewModel.navState.collectAsState()
    val mediaState by mediaViewModel.mediaState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // ── Full-screen map ───────────────────────────────────────────────────
        MapLibreMapView(
            navState = navState,
            modifier = Modifier.fillMaxSize()
        )

        // ── Curved gradient vignette overlay (top) ───────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        0f to DarkBackground.copy(alpha = 0.6f),
                        0.18f to DarkBackground.copy(alpha = 0f)
                    )
                )
                .padding(bottom = 80.dp)            // room for the bar
        )

        // ── Curved gradient vignette overlay (bottom) ────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0f to DarkBackground.copy(alpha = 0f),
                        0.6f to DarkBackground.copy(alpha = 0.9f)
                    )
                )
                .padding(top = 80.dp)
        )

        // ── Top: AI assistant bar ─────────────────────────────────────────────
        AiAssistantBar(
            navInstruction = if (navState.isDemoRunning) navState.nextInstruction else "",
            modifier       = Modifier.align(Alignment.TopCenter)
        )

        // ── Bottom stack: speed HUD + media card ──────────────────────────────
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SpeedHud(
                navState = navState,
                modifier = Modifier.align(Alignment.Start)
            )

            MediaCard(
                mediaState    = mediaState,
                onPlayPause   = mediaViewModel::playPause,
                onSkipNext    = mediaViewModel::skipNext,
                onSkipPrevious = mediaViewModel::skipPrevious
            )
        }

        // ── Demo FAB (bottom-end corner) ──────────────────────────────────────
        DemoFab(
            isRunning = navState.isDemoRunning,
            onClick   = navViewModel::toggleDemo,
            modifier  = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
                .padding(end = 20.dp, bottom = 130.dp)   // above media card
        )

        // ── "DEMO MODE" badge ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = navState.isDemoRunning,
            enter   = fadeIn(),
            exit    = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .systemBarsPadding()
                .padding(end = 16.dp, top = 16.dp)
        ) {
            Text(
                text       = "DEMO",
                style      = MaterialTheme.typography.labelSmall,
                color      = AccentAmber,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier
                    .background(AccentAmber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .border(1.dp, AccentAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

// ── Demo FAB ─────────────────────────────────────────────────────────────────

@Composable
private fun DemoFab(
    isRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon  = if (isRunning) Icons.Default.Stop else Icons.Default.DirectionsCar
    val label = if (isRunning) "Stop" else "Start\nDemo"
    val tint  = if (isRunning) TextPrimary else NeonBlue
    val bg    = if (isRunning) GlassDark else NeonBlueGlow

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(
                1.dp,
                if (isRunning) GlassBorder else NeonBlue.copy(alpha = 0.6f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = tint,
            modifier           = Modifier.size(26.dp)
        )
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelSmall,
            color      = tint,
            fontWeight = FontWeight.SemiBold
        )
    }
}

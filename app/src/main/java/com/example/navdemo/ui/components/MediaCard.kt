package com.example.navdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.navdemo.data.model.MediaState
import com.example.navdemo.ui.theme.AccentGreen
import com.example.navdemo.ui.theme.GlassBorder
import com.example.navdemo.ui.theme.GlassDark
import com.example.navdemo.ui.theme.NeonBlue
import com.example.navdemo.ui.theme.NeonBlueGlow
import com.example.navdemo.ui.theme.TextPrimary
import com.example.navdemo.ui.theme.TextSecondary

/**
 * YouTube-Music-style bottom media card with frosted-glass styling.
 *
 * Shows current track info + progress bar + transport controls.
 * Delegates all actions back via lambdas so the card is stateless/reusable.
 */
@Composable
fun MediaCard(
    mediaState: MediaState,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(GlassDark.copy(alpha = 0.92f), GlassDark)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(GlassBorder, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // ── Row: album art placeholder + track info + controls ────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Album art placeholder
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(NeonBlueGlow, shape = RoundedCornerShape(10.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint               = NeonBlue,
                        modifier           = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Track info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text     = mediaState.trackTitle,
                        style    = MaterialTheme.typography.titleMedium,
                        color    = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text     = "${mediaState.artistName} · ${mediaState.albumName}",
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Transport controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ControlButton(onClick = onSkipPrevious) {
                        Icon(
                            imageVector        = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint               = TextSecondary,
                            modifier           = Modifier.size(22.dp)
                        )
                    }

                    // Play / Pause – larger, neon-highlighted
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(NeonBlue, CircleShape)
                            .clickable(onClick = onPlayPause),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (mediaState.isPlaying)
                                Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (mediaState.isPlaying) "Pause" else "Play",
                            tint    = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    ControlButton(onClick = onSkipNext) {
                        Icon(
                            imageVector        = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint               = TextSecondary,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Progress bar ──────────────────────────────────────────────────
            LinearProgressIndicator(
                progress        = { mediaState.progress },
                modifier        = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color           = if (mediaState.isPlaying) NeonBlue else AccentGreen,
                trackColor      = GlassBorder
            )
        }
    }
}

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(NeonBlueGlow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

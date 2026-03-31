package com.example.navdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navdemo.data.model.NavState
import com.example.navdemo.ui.theme.AccentGreen
import com.example.navdemo.ui.theme.GlassBorder
import com.example.navdemo.ui.theme.GlassDark
import com.example.navdemo.ui.theme.NeonBlue
import com.example.navdemo.ui.theme.TextPrimary
import com.example.navdemo.ui.theme.TextSecondary

/**
 * Compact heads-up display pill shown over the map (bottom-start corner).
 *
 * Displays:
 * - Current speed in km/h with a neon green accent
 * - ETA and distance remaining
 *
 * Designed to be semi-transparent so the map remains visible beneath it.
 */
@Composable
fun SpeedHud(
    navState: NavState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // ── Speed pill ────────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(GlassDark.copy(alpha = 0.95f), GlassDark)
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Speed,
                contentDescription = null,
                tint               = AccentGreen,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text       = navState.speedKmh.toString(),
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                color      = AccentGreen
            )
            Text(
                text  = "km/h",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // ── ETA / distance pill ───────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(GlassDark.copy(alpha = 0.95f), GlassDark)
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Navigation,
                    contentDescription = null,
                    tint               = NeonBlue,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text  = "ETA",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text       = navState.eta,
                style      = MaterialTheme.typography.titleLarge,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = formatDistance(navState.distanceToNext),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

private fun formatDistance(metres: Int): String = when {
    metres >= 1_000 -> "${"%.1f".format(metres / 1_000f)} km"
    else            -> "$metres m"
}

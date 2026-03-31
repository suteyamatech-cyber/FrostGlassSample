package com.example.navdemo.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.navdemo.data.FakeRouteData
import com.example.navdemo.ui.theme.GlassBorder
import com.example.navdemo.ui.theme.GlassDark
import com.example.navdemo.ui.theme.NeonBlue
import com.example.navdemo.ui.theme.NeonBlueGlow
import com.example.navdemo.ui.theme.TextPrimary
import com.example.navdemo.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Frosted-glass top bar that shows AI navigation assistant messages.
 *
 * Messages cycle through [FakeRouteData.aiMessages] with a slide animation,
 * but can be overridden by live [navInstruction] when the demo is running.
 *
 * Extensibility: swap the message source with a real LLM / STT response
 * stream by collecting a Flow<String> in the parent and passing it in.
 */
@Composable
fun AiAssistantBar(
    navInstruction: String,
    modifier: Modifier = Modifier
) {
    // When there's no live instruction, cycle through AI tip messages
    var tipIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(8_000L)
            tipIndex = (tipIndex + 1) % FakeRouteData.aiMessages.size
        }
    }

    val displayText = navInstruction.ifBlank {
        FakeRouteData.aiMessages[tipIndex]
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Glass card
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(GlassDark, GlassDark.copy(alpha = 0.85f))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(GlassBorder, Color.Transparent)),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // AI icon with neon glow tint
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(NeonBlueGlow, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.SmartToy,
                    contentDescription = "AI assistant",
                    tint               = NeonBlue,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text  = "AI Navigator",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                AnimatedContent(
                    targetState = displayText,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { -it / 2 }) togetherWith
                                (fadeOut() + slideOutVertically { it / 2 })
                    },
                    label = "ai_message"
                ) { text ->
                    Text(
                        text     = text,
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = TextPrimary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

package com.example.navdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NovaDriveColorScheme = darkColorScheme(
    primary          = NeonBlue,
    onPrimary        = DarkBackground,
    primaryContainer = NeonBlueDim,
    secondary        = AccentGreen,
    onSecondary      = DarkBackground,
    background       = DarkBackground,
    onBackground     = TextPrimary,
    surface          = DarkSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = DarkCard,
    outline          = NeonBlueDim,
    error            = AccentRed
)

@Composable
fun NovaDriveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NovaDriveColorScheme,
        typography  = NovaDriveTypography,
        content     = content
    )
}

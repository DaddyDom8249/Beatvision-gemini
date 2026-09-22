package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = CinematicBlack,
    primaryContainer = ElectricCyanDark,
    onPrimaryContainer = TextPrimary,
    secondary = AmberHalogen,
    onSecondary = CinematicBlack,
    secondaryContainer = Color(0xFF5D4000),
    onSecondaryContainer = AmberHalogen,
    tertiary = NeonViolet,
    onTertiary = Color.White,
    background = CinematicBlack,
    onBackground = TextPrimary,
    surface = CinematicDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinematicCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderAccent,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

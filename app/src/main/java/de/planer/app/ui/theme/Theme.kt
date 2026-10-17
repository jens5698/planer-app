package de.planer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF4772FA)

// Angelehnt an TickTick: schwarzer Hintergrund, dunkelgraue Karten, kräftiges Blau als Akzent.
private val Dark = darkColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    background = Color.Black,
    onBackground = Color(0xFFF2F2F2),
    surface = Color.Black,
    onSurface = Color(0xFFF2F2F2),
    surfaceContainer = Color(0xFF1C1C1E),
    surfaceContainerHigh = Color(0xFF2A2A2D),
    surfaceContainerLow = Color(0xFF111112),
    onSurfaceVariant = Color(0xFFB0B0B5),
    outline = Color(0xFF8A8A8F),
    outlineVariant = Color(0xFF3A3A3D),
    tertiary = Color(0xFF26A69A),
    error = Color(0xFFE5484D),
)

private val Light = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    background = Color(0xFFF2F3F7),
    surface = Color(0xFFF2F3F7),
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFE9EAEE),
    surfaceContainerLow = Color(0xFFF7F7FA),
    onSurfaceVariant = Color(0xFF5F6066),
    outline = Color(0xFF8A8A8F),
    outlineVariant = Color(0xFFDADBE0),
    tertiary = Color(0xFF00897B),
    error = Color(0xFFD52B24),
)

@Composable
fun PlanerTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}

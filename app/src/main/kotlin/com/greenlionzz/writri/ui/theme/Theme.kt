package com.greenlionzz.writri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001A72),
    secondary = Color(0xFF5C6BC0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9DCFF),
    onSecondaryContainer = Color(0xFF14237B),
    tertiary = Color(0xFF7C4DFF),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1F1D1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1F1D1E),
    surfaceVariant = Color(0xFFE5E2EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFBA1A1A)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF001A72),
    primaryContainer = Color(0xFF1E4989),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF7986CB),
    onSecondary = Color(0xFF14237B),
    secondaryContainer = Color(0xFF34459B),
    onSecondaryContainer = Color(0xFFD9DCFF),
    tertiary = Color(0xFFB388FF),
    onTertiary = Color(0xFF20005A),
    background = Color(0xFF1F1D1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1F1D1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCCC2DC),
    error = Color(0xFFFFB4AB)
)

val SepiaColorScheme = lightColorScheme(
    primary = Color(0xFF8B6914),
    onPrimary = Color(0xFFFFFBFF),
    primaryContainer = Color(0xFFD4C0A1),
    onPrimaryContainer = Color(0xFF3D2F0F),
    secondary = Color(0xFFA67C52),
    onSecondary = Color(0xFF3D2F0F),
    secondaryContainer = Color(0xFFE8D5B8),
    onSecondaryContainer = Color(0xFF3D2F0F),
    background = Color(0xFFF5F0E1),
    onBackground = Color(0xFF3D2F0F),
    surface = Color(0xFFF5F0E1),
    onSurface = Color(0xFF3D2F0F),
    surfaceVariant = Color(0xFFE8D5B8),
    onSurfaceVariant = Color(0xFF5D4A1F)
)

@Composable
fun WritriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = "dark",
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        "dark" -> DarkColorScheme
        "sepia" -> SepiaColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
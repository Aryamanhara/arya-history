package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGoldLight,
    onPrimary = Color(0xFF332500),
    primaryContainer = Color(0xFF4A3800),
    onPrimaryContainer = Color(0xFFFFDF94),
    secondary = HeritageBlueLight,
    onSecondary = Color.White,
    secondaryContainer = SlateDarkCard,
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = TerracottaRust,
    onTertiary = Color.White,
    background = SlateDarkBackground,
    onBackground = TextPrimaryDark,
    surface = SlateDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateDarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = HeritageBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF001944),
    secondary = AntiqueGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDEA1),
    onSecondaryContainer = Color(0xFF281900),
    tertiary = TerracottaRust,
    onTertiary = Color.White,
    background = ParchmentLight,
    onBackground = TextPrimaryLight,
    surface = ParchmentCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1EDE4),
    onSurfaceVariant = Color(0xFF49454E),
    outline = ParchmentBorder
)

@Composable
fun AryaHistoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted academic theme by default for identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

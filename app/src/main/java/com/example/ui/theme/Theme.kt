package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BentoViolet,
    secondary = BentoOrange,
    tertiary = BentoEmerald,
    background = BentoBackground,
    surface = BentoSurface,
    onPrimary = BentoBackground,
    onSecondary = BentoBackground,
    onTertiary = BentoBackground,
    onBackground = LightBackground,
    onSurface = LightBackground,
    outline = BentoBorder,
    error = BentoRed
)

private val LightColorScheme = lightColorScheme(
    primary = BentoVioletDark,
    secondary = BentoOrangeDark,
    tertiary = BentoEmerald,
    background = LightBackground,
    surface = CardLight,
    onPrimary = CardLight,
    onSecondary = CardLight,
    onTertiary = CardLight,
    onBackground = BentoBackground,
    onSurface = BentoBackground,
    outline = CardLight.copy(alpha = 0.1f),
    error = BentoRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Always keep original brand palette or bento palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Force dark bento theme as default is pitch dark grid!
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

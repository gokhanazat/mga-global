package com.mgacreative.mgaglobal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = DarkNavy,
    surface = DeepNavy,
    onBackground = LightGray,
    onSurface = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    surface = Surface,
    onBackground = OnBackground,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFE9ECEF)
)

@Composable
fun TradeBridgeTheme(
    darkTheme: Boolean = false, // Force light theme by default
    content: @Composable () -> Unit
) {
    // Simplify for now: use standard SansSerif
    val appFontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
    
    // Always use LightColorScheme as requested
    val colorScheme = LightColorScheme

    val defaultTypography = MaterialTheme.typography
    // Apply font family mapping
    fun applyFont(style: androidx.compose.ui.text.TextStyle) = style.copy(
        fontFamily = appFontFamily
    )

    val typography = defaultTypography.copy(
        displayLarge = applyFont(defaultTypography.displayLarge),
        displayMedium = applyFont(defaultTypography.displayMedium),
        displaySmall = applyFont(defaultTypography.displaySmall),
        headlineLarge = applyFont(defaultTypography.headlineLarge),
        headlineMedium = applyFont(defaultTypography.headlineMedium),
        headlineSmall = applyFont(defaultTypography.headlineSmall),
        titleLarge = applyFont(defaultTypography.titleLarge),
        titleMedium = applyFont(defaultTypography.titleMedium),
        titleSmall = applyFont(defaultTypography.titleSmall),
        bodyLarge = applyFont(defaultTypography.bodyLarge),
        bodyMedium = applyFont(defaultTypography.bodyMedium),
        bodySmall = applyFont(defaultTypography.bodySmall),
        labelLarge = applyFont(defaultTypography.labelLarge),
        labelMedium = applyFont(defaultTypography.labelMedium),
        labelSmall = applyFont(defaultTypography.labelSmall)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}


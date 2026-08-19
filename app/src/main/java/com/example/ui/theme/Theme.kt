package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppThemeMode
import com.example.data.model.ColorPalette

private val EmeraldLight = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = EmeraldOnPrimaryLight,
    primaryContainer = EmeraldPrimaryContainerLight,
    onPrimaryContainer = EmeraldOnPrimaryContainerLight,
    secondary = EmeraldSecondaryLight,
    background = EmeraldBackgroundLight,
    surface = EmeraldSurfaceLight,
    surfaceVariant = EmeraldSurfaceVariantLight,
    onSurface = EmeraldOnSurfaceLight,
    outline = LightDivider
)

private val EmeraldDark = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldPrimaryContainerDark,
    onPrimaryContainer = EmeraldOnPrimaryContainerDark,
    secondary = EmeraldSecondaryDark,
    background = EmeraldBackgroundDark,
    surface = EmeraldSurfaceDark,
    surfaceVariant = EmeraldSurfaceVariantDark,
    onSurface = EmeraldOnSurfaceDark,
    outline = DarkDivider
)

private val GoldLight = lightColorScheme(
    primary = GoldPrimaryLight,
    onPrimary = GoldOnPrimaryLight,
    primaryContainer = GoldPrimaryContainerLight,
    background = GoldBackgroundLight,
    surface = GoldSurfaceLight,
    onSurface = GoldOnSurfaceLight,
    outline = LightDivider
)

private val GoldDark = darkColorScheme(
    primary = GoldPrimaryDark,
    onPrimary = GoldOnPrimaryDark,
    primaryContainer = GoldPrimaryContainerDark,
    background = GoldBackgroundDark,
    surface = GoldSurfaceDark,
    onSurface = GoldOnSurfaceDark,
    outline = DarkDivider
)

private val MidnightLight = lightColorScheme(
    primary = MidnightPrimaryLight,
    onPrimary = MidnightOnPrimaryLight,
    primaryContainer = MidnightPrimaryContainerLight,
    background = MidnightBackgroundLight,
    surface = MidnightSurfaceLight,
    onSurface = MidnightOnSurfaceLight,
    outline = LightDivider
)

private val MidnightDark = darkColorScheme(
    primary = MidnightPrimaryDark,
    onPrimary = MidnightOnPrimaryDark,
    primaryContainer = MidnightPrimaryContainerDark,
    background = MidnightBackgroundDark,
    surface = MidnightSurfaceDark,
    onSurface = MidnightOnSurfaceDark,
    outline = DarkDivider
)

private val TealLight = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = TealOnPrimaryLight,
    primaryContainer = TealPrimaryContainerLight,
    background = TealBackgroundLight,
    surface = TealSurfaceLight,
    onSurface = TealOnSurfaceLight,
    outline = LightDivider
)

private val TealDark = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealPrimaryContainerDark,
    background = TealBackgroundDark,
    surface = TealSurfaceDark,
    onSurface = TealOnSurfaceDark,
    outline = DarkDivider
)

@Composable
fun AzkarMuslimTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorPalette: ColorPalette = ColorPalette.EMERALD,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when (colorPalette) {
        ColorPalette.EMERALD -> if (isDark) EmeraldDark else EmeraldLight
        ColorPalette.GOLD -> if (isDark) GoldDark else GoldLight
        ColorPalette.MIDNIGHT -> if (isDark) MidnightDark else MidnightLight
        ColorPalette.TEAL -> if (isDark) TealDark else TealLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

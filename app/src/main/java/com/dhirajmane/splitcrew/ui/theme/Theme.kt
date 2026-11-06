package com.dhirajmane.splitcrew.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueDark,
    onPrimary = BrandNavy,
    secondary = BrandTealDark,
    tertiary = BrandGreenDark,
    background = AppBackgroundDark,
    surface = AppSurfaceDark,
    surfaceVariant = AppOutlineDark,
    error = OweCoral
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    secondary = BrandTeal,
    tertiary = BrandGreen,
    background = AppBackgroundLight,
    surface = AppSurfaceLight,
    surfaceVariant = AppOutlineLight,
    error = OweCoral
)

@Composable
fun SplitCrewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
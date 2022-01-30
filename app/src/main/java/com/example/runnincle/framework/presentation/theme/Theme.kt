package com.example.runnincle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = AeroBlue,
    primaryVariant = MintGreen,
    onPrimary = Cultured,
    secondary = PaleEggBlue,
    secondaryVariant = SkyBlue,
    onSecondary = PatrickBlue,
    error = Jasper,
    onError = Cultured,
    background = Cultured,
    onBackground = Gunmetal,
    surface = Color.White,
    onSurface = Gunmetal,
)

private val LightColorPalette = lightColors(
    primary = AeroBlue,
    primaryVariant = MintGreen,
    onPrimary = Cultured,
    secondary = PaleEggBlue,
    secondaryVariant = SkyBlue,
    onSecondary = PatrickBlue,
    error = Jasper,
    onError = Cultured,
    background = Cultured,
    onBackground = Gunmetal,
    surface = Color.White,
    onSurface = Gunmetal,
)

@Composable
fun RunnincleTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = InfinitySansTypography,
        shapes = Shapes,
        content = content
    )
}
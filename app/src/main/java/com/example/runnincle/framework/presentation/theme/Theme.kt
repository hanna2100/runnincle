package com.example.runnincle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.siddroid.holi.colors.MaterialColor

private val DarkColorPalette = darkColors(
    primary = YankeesBlue,
    primaryVariant = Charcoal,
    onPrimary = Cultured,
    secondary = PaleEggBlue,
    secondaryVariant = SkyBlue,
    onSecondary = PatrickBlue,
    error = Jasper,
    onError = Cultured,
    background = Cultured,
    onBackground = Gunmetal,
    surface = White,
    onSurface = Gunmetal,
)

private val LightColorPalette = lightColors(
    primary = YankeesBlue,
    primaryVariant = Charcoal,
    onPrimary = Cultured,
    secondary = PaleEggBlue,
    secondaryVariant = SkyBlue,
    onSecondary = PatrickBlue,
    error = Jasper,
    onError = Cultured,
    background = Cultured,
    onBackground = Gunmetal,
    surface = White,
    onSurface = Gunmetal,
)

@Composable
fun RunnincleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkSystemBar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = NanumSquareTypography,
        shapes = Shapes,
        content = content
    )

    val systemUiController = rememberSystemUiController()
    val background = colors.background
    val primary = colors.primary

    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (darkSystemBar) primary else background,
            darkIcons = !darkSystemBar
        )
        systemUiController.setNavigationBarColor(
            color = if (!darkSystemBar) primary else background,
            darkIcons = darkSystemBar
        )
    }
}

object RippleCustomTheme: RippleTheme {

    @Composable
    override fun defaultColor(): Color = MaterialTheme.colors.primary

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = !isSystemInDarkTheme()
    )
}
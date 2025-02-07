package com.softyorch.famousquotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = DarkBackgroundColor,
    onBackground = LightSmoke,
    onPrimary = LightTextStandardColor,
    onSecondary = LightTextShadowColor,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = DarkBackgroundColor,
    onBackground = DarkSmoke,
    onPrimary = DarkTextStandardColor,
    onSecondary = DarkTextShadowColor,
)

var AppColorSchema: AppColorScheme = DarkColorSchemeApp()

@Composable
fun FamousQuotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    val view = LocalView.current
    val window = (view.context as Activity).window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, view).let { controller ->
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    AppColorSchema = if (darkTheme) DarkColorSchemeApp() else LightColorSchemeApp()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
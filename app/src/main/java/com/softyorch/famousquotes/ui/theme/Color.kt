package com.softyorch.famousquotes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.softyorch.famousquotes.BuildConfig

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PrimaryColor = Color(BuildConfig.PRIMARY_COLOR)
val SecondaryColor = Color(BuildConfig.SECONDARY_COLOR)
val TertiaryColor = Color(BuildConfig.TERTIARY_COLOR)
val BackgroundColor = Color(0xFFC1B79A)
val PanelColor = Color(BuildConfig.PANEL_COLOR)
val AuxColor = Color(BuildConfig.AUX_COLOR)

@Composable
fun brushBackGround(): Brush = Brush.linearGradient(
    listOf(
        BackgroundColor,
        Color.White,
        BackgroundColor,
        Color.White,
        BackgroundColor
    )
)
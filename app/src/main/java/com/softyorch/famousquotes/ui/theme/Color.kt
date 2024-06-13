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

 // Configured colors in gradle flavors
/*
val HistoricalPrimary = Color(0xFFFFB005)
val HistoricalSecondary = Color(0xFFFFD966)
val HistoricalBackground = Color(0xFF00253A)

val BiblicalPrimary = Color(0xFF3182BD)
val BiblicalSecondary = Color(0xFF6BAED6)
val BiblicalBackground = Color(0xFF7FA0BB)

val UpliftingPrimary = Color(0xFF99CE00)
val UpliftingSecondary = Color(0xFFBAD56B)
val UpliftingBackground = Color(0xFF00253A)
*/


val WhiteSmoke = Color(0xFFE6E6E6)

val PrimaryColor = Color(BuildConfig.PRIMARY_COLOR)
val SecondaryColor = Color(BuildConfig.SECONDARY_COLOR)
val BackgroundColor = Color(BuildConfig.BACKGROUND_COLOR)

@Composable
fun brushBackGround(): Brush = Brush.linearGradient(
    listOf(
        BackgroundColor.copy(alpha = 0.6f),
        BackgroundColor,
        BackgroundColor,
        BackgroundColor.copy(alpha = 0.6f),
    )
)

@Composable
fun brushBackGround2(): Brush = Brush.verticalGradient(
    listOf(
        BackgroundColor.copy(alpha = 0f),
        BackgroundColor.copy(alpha = 0.3f),
        BackgroundColor.copy(alpha = 0.7f),
        BackgroundColor.copy(alpha = 0.9f),
        BackgroundColor,
        //-------------- 50% ------------//
        BackgroundColor,
        BackgroundColor,
        BackgroundColor,
        BackgroundColor,
        BackgroundColor,
    )
)


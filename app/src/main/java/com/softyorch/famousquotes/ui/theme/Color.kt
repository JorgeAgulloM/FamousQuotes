package com.softyorch.famousquotes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.softyorch.famousquotes.BuildConfig

// Configured colors in gradle flavors
/*
val HistoricalPrimary = Color(0xFFFFB005)
val HistoricalSecondary = Color(0xFFFFD966)
val HistoricalBackground = Color(0xFF00253A)

val BiblicalPrimary = Color(0xFF3182BD)
val BiblicalSecondary = Color(0xFF6BAED6)
val BiblicalBackground = Color(0xFF00253A)

val UpliftingPrimary = Color(0xFF99CE00)
val UpliftingSecondary = Color(0xFFBAD56B)
val UpliftingBackground = Color(0xFF00253A)
*/


/* Light Colors */
val LightSmoke = Color(0xFFE6E6E6)
val LightLikeColor = Color(0xFFBE0000)
val LightFavoriteColor = Color(0xFFC8C800)
val LightDisabledIconButtonColor = Color(0xFF737373)
val LightTextStandardColor = Color(0xE6000000)
val LightTextShadowColor = Color(0x4D000000)
val LightIconColor = Color(0xFF1E1E1E)
val LightBackgroundColor = Color(0xFFEAEAEA)
val LightWhiteSmoke = Color(0xFFFFFFFF)
val LightCardColor = Color(0xFFD3D8DC)

/* Dark Colors */
val DarkSmoke = Color(0xFF3D3D3D)
val DarkLikeColor = Color(0xFFBE0000)
val DarkFavoriteColor = Color(0xFFC8C800)
val DarkDisabledIconButtonColor = Color(0xFF737373)
val DarkTextStandardColor = Color(0xE6FFFFFF)
val DarkTextShadowColor = Color(0x4DFFFFFF)
val DarkIconColor = Color(0xFFE6E6E6)
val DarkBackgroundColor = Color(0xFF00253A)
val DarkWhiteSmoke = Color(0xFFE6E6E6)
val DarkCardColor = Color(0xFF2F373B)

/* Common Colors */
val PrimaryColor = Color(BuildConfig.PRIMARY_COLOR)
val SecondaryColor = Color(BuildConfig.SECONDARY_COLOR)

fun LightColorSchemeApp(
    primary: Color = PrimaryColor,
    secondary: Color = SecondaryColor,
    background: Color = LightBackgroundColor,
    smoke: Color = LightSmoke,
    text: Color = LightTextStandardColor,
    shadowText: Color = LightTextShadowColor,
    likeColor: Color = LightLikeColor,
    favoriteColor: Color = LightFavoriteColor,
    disabledIconButtonColor: Color = LightDisabledIconButtonColor,
    iconColor: Color = LightIconColor,
    whiteSmoke: Color = LightWhiteSmoke
): AppColorScheme = AppColorScheme(
    primary = primary,
    secondary = secondary,
    background = background,
    smoke = smoke,
    text = text,
    shadowText = shadowText,
    likeColor = likeColor,
    favoriteColor = favoriteColor,
    disabledIconButtonColor = disabledIconButtonColor,
    iconColor = iconColor,
    whiteSmoke = whiteSmoke,
    cardColor = LightCardColor
)

fun DarkColorSchemeApp(
    primary: Color = PrimaryColor,
    secondary: Color = SecondaryColor,
    background: Color = DarkBackgroundColor,
    smoke: Color = DarkSmoke,
    text: Color = DarkTextStandardColor,
    shadowText: Color = DarkTextShadowColor,
    likeColor: Color = DarkLikeColor,
    favoriteColor: Color = DarkFavoriteColor,
    disabledIconButtonColor: Color = DarkDisabledIconButtonColor,
    iconColor: Color = DarkIconColor,
    whiteSmoke: Color = DarkWhiteSmoke
): AppColorScheme = AppColorScheme(
    primary = primary,
    secondary = secondary,
    background = background,
    smoke = smoke,
    text = text,
    shadowText = shadowText,
    likeColor = likeColor,
    favoriteColor = favoriteColor,
    disabledIconButtonColor = disabledIconButtonColor,
    iconColor = iconColor,
    whiteSmoke = whiteSmoke,
    cardColor = DarkCardColor
)

@Composable
fun brushBackGround(): Brush = Brush.linearGradient(
    listOf(
        AppColorSchema.background.copy(alpha = 0.6f),
        AppColorSchema.background,
        AppColorSchema.background,
        AppColorSchema.background.copy(alpha = 0.6f),
    )
)

@Composable
fun brushBackGround2(): Brush = Brush.verticalGradient(
    listOf(
        AppColorSchema.background.copy(alpha = 0f),
        AppColorSchema.background.copy(alpha = 0.3f),
        AppColorSchema.background.copy(alpha = 0.7f),
        AppColorSchema.background.copy(alpha = 0.9f),
        AppColorSchema.background,
        //-------------- 50% ------------//
        AppColorSchema.background,
        AppColorSchema.background,
        AppColorSchema.background,
        AppColorSchema.background,
        AppColorSchema.background,
    )
)


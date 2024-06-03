package com.softyorch.famousquotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.softyorch.famousquotes.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Montserrat Alternates")
val FontMontserrat = FontFamily(
    Font(
        googleFont = fontName,
        fontProvider = provider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Italic
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val MyTypography = Typography(
    labelMedium = TextStyle(
        fontFamily = FontMontserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Color.DarkGray
    ),
    labelLarge = TextStyle(
        fontFamily = FontMontserrat,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        fontSize = 16.sp,
        color = Color.DarkGray
    ),
    displayLarge = TextStyle(
        fontFamily = FontMontserrat,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = Color.DarkGray
    ),
    bodyLarge = TextStyle(
        fontFamily = FontMontserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.DarkGray
    )
)


package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextBody(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            style = MyTypography.displayLarge.copy(
                color = AppColorSchema.text,
                shadow = Shadow(
                    color = AppColorSchema.smoke,
                    blurRadius = 8f
                )
            )
        )
    }
}
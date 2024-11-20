package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextBody(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            style = MyTypography.displayLarge.copy(
                color = Color.White
            )
        )
    }
}
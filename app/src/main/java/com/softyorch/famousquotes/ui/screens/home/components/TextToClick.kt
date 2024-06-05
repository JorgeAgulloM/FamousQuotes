package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextToClick(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            color = Color.White,
            style = MyTypography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

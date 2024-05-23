package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextInfo(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MyTypography.labelLarge
        )
    }
}
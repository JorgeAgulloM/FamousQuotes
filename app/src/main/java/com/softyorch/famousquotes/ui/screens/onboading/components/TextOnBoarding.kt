package com.softyorch.famousquotes.ui.screens.onboading.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun TextOnBoarding(startText: String, text: String) {
    Column {
        SpacerHeight()
        Text(
            text = startText,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColorSchema.text
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColorSchema.text
        )
    }
}

@Composable
fun TextOnBoarding(text: String) {
    Column {
        SpacerHeight()
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = AppColorSchema.text)
    }
}
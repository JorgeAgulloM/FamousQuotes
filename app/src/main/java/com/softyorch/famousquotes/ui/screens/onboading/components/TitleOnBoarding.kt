package com.softyorch.famousquotes.ui.screens.onboading.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun TitleOnBoarding(text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SpacerHeight()
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center
            ),
            color = AppColorSchema.text,
            modifier = Modifier.fillMaxWidth()
        )
        SpacerHeight()
    }
}
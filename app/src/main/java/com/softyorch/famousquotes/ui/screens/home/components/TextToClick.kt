package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextToClick(text: String, onclick: () -> Unit = {}) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onclick() }
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        color = AppColorSchema.text,
        style = MyTypography.labelMedium,
        textAlign = TextAlign.Center
    )
}

package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedTextHome
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.TextStandardWhiteColor

@Composable
fun TextOwner(
    text: String,
    color: Color = TextStandardWhiteColor,
    isHiPadding: Boolean = true,
    onClick: () -> Unit
) {
    val padding = if (isHiPadding) 16.dp else 8.dp
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = padding)
                .clip(shape = MaterialTheme.shapes.large)
                .clickable { onClick() },
            style = MyTypography.labelLarge.copy(color = color),
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center
        )
    }
}
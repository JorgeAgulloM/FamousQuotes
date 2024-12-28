package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun ButtonApp(
    modifier: Modifier = Modifier.defaultMinSize(minHeight = 56.dp),
    text: String,
    primary: Boolean = false,
    onClick: () -> Unit
) {
    val color = if (primary) AppColorSchema.primary else AppColorSchema.cardColor
    val elevation = if (primary) 4.dp else 3.dp

    Button(
        onClick = { onClick() },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            hoveredElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors().copy(containerColor = color),
        shape = RoundedCornerShape(25),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MyTypography.bodyLarge.copy(
                color = AppColorSchema.text
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonAppPreview() {
    ButtonApp(text = "Button") {}
}

@Preview(showBackground = true)
@Composable
fun ButtonAppPreviewPrimary() {
    ButtonApp(text = "Button", primary = true) {}
}

@Preview(showBackground = true)
@Composable
fun ButtonAppPreviewPrimaryModifier() {
    ButtonApp(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        text = "Button",
        primary = true
    ) {}
}

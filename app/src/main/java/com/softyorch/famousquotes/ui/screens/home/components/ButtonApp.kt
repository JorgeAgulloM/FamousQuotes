package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.SecondaryColor

@Composable
fun ButtonApp(text: String, primary: Boolean = false, onClick: () -> Unit) {
    val color = if (primary) SecondaryColor else SecondaryColor.copy(alpha = 0.6f)
    val elevation = if (primary) 4.dp else 1.dp

    Button(
        onClick = { onClick() },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        colors = ButtonDefaults.buttonColors().copy(containerColor = color)
    ) {
        Text(text = text, style = MyTypography.bodyLarge)
    }
}

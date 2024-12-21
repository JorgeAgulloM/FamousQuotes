package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor

@Composable
fun ButtonApp(text: String, primary: Boolean = false, onClick: () -> Unit) {
    val color = if (primary) PrimaryColor else Color.LightGray
    val elevation = if (primary) 4.dp else 1.dp

    Button(
        onClick = { onClick() },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        colors = ButtonDefaults.buttonColors().copy(containerColor = color),
        shape = RoundedCornerShape(25),
        modifier = Modifier.defaultMinSize(minHeight = 56.dp)
    ) {
        Text(
            text = text,
            style = MyTypography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

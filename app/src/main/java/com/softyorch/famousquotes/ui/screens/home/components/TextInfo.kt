package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.theme.MyTypography

@Composable
fun TextInfo(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MyTypography.labelLarge.copy(
                shadow = Shadow(color = AppColorSchema.shadowText, offset = Offset(2f, 2f)),
                color = AppColorSchema.text,
                textAlign = TextAlign.Start
            )
        )
    }
}

@Composable
fun TextInfoApp(text: String, size: Int, offsetY: Int = 0, color: Color = AppColorSchema.text) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 4.dp).offset(y = offsetY.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = size.sp,
                fontWeight = FontWeight.ExtraBold,
                shadow = Shadow(color = AppColorSchema.shadowText, offset = Offset(2f, 2f)),
                color = color,
                textAlign = TextAlign.Center,
                textGeometricTransform = TextGeometricTransform(1.2f)
            )
        )
    }
}

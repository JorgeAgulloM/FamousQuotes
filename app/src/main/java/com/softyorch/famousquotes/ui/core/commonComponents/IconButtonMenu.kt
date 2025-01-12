package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = AppColorSchema.secondary.copy(alpha = 0.6f),
    colorIcon: Color = AppColorSchema.whiteSmoke,
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    shadowOn: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val selectedColor = AppColorSchema.primary
    val selectColor = if (isSelected) selectedColor
    else if (colorIcon != AppColorSchema.whiteSmoke)
        colorIcon
    else AppColorSchema.whiteSmoke

    if (isVisible) IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(48.dp),
        enabled = isEnabled
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (shadowOn) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = color, shape = CircleShape)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = cDescription,
                modifier = Modifier.size(32.dp),
                tint = selectColor,
            )
        }
    }
}

package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.SecondaryColor

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = SecondaryColor,
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    shadowOn: Boolean = false,
    onClick: () -> Unit,
) {
    if (isVisible) IconButton(
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(contentColor = color),
        modifier = Modifier.padding(end = 4.dp),
        enabled = isEnabled
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (shadowOn) Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .size(40.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = cDescription,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
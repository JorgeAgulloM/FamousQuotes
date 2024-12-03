package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = SecondaryColor,
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    shadowOn: Boolean = true,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val selectColor = if (isSelected) WhiteSmoke.copy(alpha = 0.4f)
    else WhiteSmoke

    if (isVisible) IconButton(
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(contentColor = selectColor),
        modifier = Modifier
            .padding(end = 6.dp)
            .background(color = color, shape = MaterialTheme.shapes.medium),
        enabled = isEnabled
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (shadowOn) {
                Box(modifier = Modifier.background(color = color).size(40.dp))
            }
            Icon(
                imageVector = icon,
                contentDescription = cDescription,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

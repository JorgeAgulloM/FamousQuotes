package com.softyorch.famousquotes.ui.screens.grid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun ButtonTopBar(
    contentDescription: String,
    filterQuotes: FilterQuotes,
    icon: ImageVector,
    isSelected: Boolean,
    onClickListener: (FilterQuotes) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
            .background(
                color = BackgroundColor,
                shape = MaterialTheme.shapes.large
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
            IconButtonMenu(
                cDescription = contentDescription,
                icon = icon,
                shadowOn = false,
                isSelected = isSelected
            ) { onClickListener(filterQuotes) }
        }
    }
}
package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerWidth
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun TopBarStandard(
    modifier: Modifier,
    paddingTop: Dp,
    leftHanded: Boolean,
    textTitle: String,
    iconTitle: ImageVector,
    onUpNavigation: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = AppColorSchema.background)
            .padding(start = 8.dp, end = 8.dp, top = paddingTop),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leftHanded) IconButtonMenu(
            cDescription = "Back",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            colorIcon = AppColorSchema.iconColor
        ) { onUpNavigation() } else SpacerIconButton()
        Row(
            modifier = modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = iconTitle,
                contentDescription = "Información",
                tint = AppColorSchema.secondary
            )
            SpacerWidth(8)
            Text(
                text = textTitle, style = MaterialTheme.typography.labelLarge.copy(
                    color = AppColorSchema.text
                )
            )
        }
        if (!leftHanded) IconButtonMenu(
            cDescription = "Back",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            colorIcon = AppColorSchema.iconColor
        ) { onUpNavigation() } else SpacerIconButton()
    }
}
package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerIconButton(modifier: Modifier = Modifier, size: Int = 48) {
    Box(modifier = modifier.size(size.dp))
}

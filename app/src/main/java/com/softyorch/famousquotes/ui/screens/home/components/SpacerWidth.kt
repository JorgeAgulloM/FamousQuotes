package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerWidth(width: Int = 16) {
    Spacer(modifier = Modifier.width(width.dp))
}

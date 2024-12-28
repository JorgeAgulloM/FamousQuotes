package com.softyorch.famousquotes.ui.screens.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun InfoScreen(modifier: Modifier = Modifier, onUpNavigation: () -> Unit) {
    Scaffold(
        topBar = {
            Row(modifier = modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = modifier.clickable { onUpNavigation() }
                )
            }
        }
    ) { dp ->
        Box(modifier = modifier
            .padding(dp)
            .fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Info Screen")
        }
    }
}

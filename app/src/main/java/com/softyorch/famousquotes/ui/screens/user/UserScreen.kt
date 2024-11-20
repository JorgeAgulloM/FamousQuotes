package com.softyorch.famousquotes.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UserScreen(navigateBack: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "User Screen")
            Button(
                onClick = { navigateBack() }
            ) {
                Text(text = "Back")
            }
        }
    }
}
package com.softyorch.famousquotes.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.softyorch.famousquotes.ui.navigation.NavigationManager
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme

@Composable
fun MainApp() {
    Surface(color = Color.Black) {
        FamousQuotesTheme {
            NavigationManager()
        }
    }
}
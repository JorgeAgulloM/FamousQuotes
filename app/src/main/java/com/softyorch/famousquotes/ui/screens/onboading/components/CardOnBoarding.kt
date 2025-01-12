package com.softyorch.famousquotes.ui.screens.onboading.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun CardOnBoarding(modifier: Modifier = Modifier, composable: @Composable () -> Unit) {

    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColorSchema.cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            composable()
        }
    }
}
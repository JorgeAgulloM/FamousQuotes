package com.softyorch.famousquotes.ui.screens.onboading.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.screens.home.components.ButtonApp

@Composable
fun ControlButtonsOnBoarding(
    textButtonPrimary: String,
    onNext: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val btnModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ButtonApp(
            modifier = btnModifier,
            text = textButtonPrimary,
            primary = true
        ) { onNext() }
        ButtonApp(
            modifier = btnModifier,
            text = stringResource(R.string.on_boarding_button_action_principal_exit)
        ) { onExit() }
    }
}
package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun HeaderSubtitleApp() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TextInfoApp(
            text = stringResource(R.string.main_text_get_inspired),
            size = 22,
            color = AppColorSchema.secondary
        )
    }
}
package com.softyorch.famousquotes.ui.screens.onboading.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun ControlImageShow(steps: List<String>, selectStep: Int, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        LazyRow {
            items(steps.size) {
                RadioButton(
                    selected = it == selectStep,
                    onClick = { onClick(it) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppColorSchema.primary,
                        unselectedColor = AppColorSchema.whiteSmoke
                    )
                )
            }
        }
    }
}
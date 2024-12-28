package com.softyorch.famousquotes.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun SwitchSettings(
    isChecked: Boolean = false,
    isEnable: Boolean = true,
    titleText: String,
    descriptionText: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = AppColorSchema.primary
            ),
            enabled = isEnable
        )

        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.labelLarge.copy(color = AppColorSchema.text)
            )
            descriptionText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(color = AppColorSchema.text)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchSettingsPreviewChecked() {
    SwitchSettings(isChecked = true, titleText = "Title", descriptionText = "Description") {}
}

@Preview(showBackground = true)
@Composable
fun SwitchSettingsPreviewUnchecked() {
    SwitchSettings(isChecked = false, titleText = "Title", descriptionText = "Description") {}
}

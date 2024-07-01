package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialogApp(
    text: String,
    auxText: String? = null,
    title: String? = null,
    textBtnPositive: String? = null,
    textBtnNegative: String? = null,
    blackDismissActions: Boolean = false,
    onActions: (DialogCloseAction) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onActions(if (blackDismissActions) DISMISS else NEGATIVE) },
        modifier = Modifier.background(
            color = BackgroundColor,
            shape = MaterialTheme.shapes.extraLarge
        ),
        properties = DialogProperties(
            dismissOnBackPress = blackDismissActions,
            dismissOnClickOutside = blackDismissActions
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            if (title != null) {
                TextInfo(title)
                SpacerHeight()
            }

            TextInfo(text)
            SpacerHeight()

            if (auxText != null) {
                TextInfo(auxText)
                SpacerHeight()
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (textBtnPositive != null) ButtonApp(text = textBtnPositive, primary = true) {
                    onActions(POSITIVE)
                }
                if (textBtnNegative != null) ButtonApp(text = textBtnNegative) {
                    onActions(NEGATIVE)
                }
            }
        }
    }
}

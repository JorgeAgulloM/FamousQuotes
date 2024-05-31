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
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialogApp(
    text: String,
    auxText: String? = null,
    title: String? = null,
    textBtnOne: String? = null,
    textBtnTwo: String? = null,
    blackDismissActions: Boolean = false,
    onActions: (DialogCloseAction) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onActions(NEGATIVE) },
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.extraLarge
        ),
        properties = DialogProperties(
            dismissOnBackPress = blackDismissActions,
            dismissOnClickOutside = blackDismissActions
        )
    ) {
        Column(
            modifier = Modifier.background(
                brush = brushBackGround(),
                shape = MaterialTheme.shapes.extraLarge
            ).padding(8.dp)
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
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (textBtnOne != null) ButtonApp(text = textBtnOne) {
                    onActions(POSITIVE)
                }
                if (textBtnTwo != null) ButtonApp(text = textBtnTwo) {
                    onActions(NEGATIVE)
                }
            }
        }
    }
}

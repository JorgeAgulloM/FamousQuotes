package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.theme.brushBackGround

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoConnectionDialog(onAction: () -> Unit) {
    val infoText = stringResource(R.string.main_info_dialog_connection)

    BasicAlertDialog(onDismissRequest = { onAction() }, modifier = Modifier.semantics {
        text = AnnotatedString(text = infoText)
    }) {
        val shape = MaterialTheme.shapes.extraLarge
        Box(
            modifier = Modifier.fillMaxWidth().background(
                color = WhiteSmoke,
                shape = shape
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(brush = brushBackGround(), shape = shape)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WifiOff,
                    contentDescription = infoText,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                TextInfo(infoText)
            }
        }
    }
}

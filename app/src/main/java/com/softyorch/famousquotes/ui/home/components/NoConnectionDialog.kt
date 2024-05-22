package com.softyorch.famousquotes.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.theme.brushBackGround

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoConnectionDialog(onAction: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onAction() }) {
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
                val text = stringResource(R.string.main_info_dialog_connection)
                Icon(
                    imageVector = Icons.Outlined.WifiOff,
                    contentDescription = text,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                TextInfo(text)
            }
        }
    }
}

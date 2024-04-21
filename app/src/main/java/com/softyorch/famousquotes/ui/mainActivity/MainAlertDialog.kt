package com.softyorch.famousquotes.ui.mainActivity

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun MainAlertDialog(onAction: (AlertState) -> Unit) {
    val appName = stringResource(R.string.app_name)
    AlertDialog(
        onDismissRequest = { onAction(AlertState.Dismiss) },
        dismissButton = { AlertButton("Salir de la APP") { onAction(AlertState.Dismiss) } },
        confirmButton = { AlertButton("Actualizar") { onAction(AlertState.Update) } },
        title = { Text(text = "Actualización Necesaria!!") },
        text = { Text(text = "¡$appName ha recibido una actualización importante!\nTe recomendamos que actualices la aplicación para continuar disfrutando de todas sus funciones.\n¡No te pierdas las nuevas características y mejoras!\nActualiza ahora para asegurarte de que puedas acceder a tu aplicación sin problemas.") },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Update,
                contentDescription = "Update",
                modifier = Modifier.size(48.dp)
            )
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = BackgroundColor,
        iconContentColor = MaterialTheme.colorScheme.primary,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            securePolicy = SecureFlagPolicy.SecureOn
        )
    )
}

@Composable
fun AlertButton(text: String, onclick: () -> Unit) {
    Button(onClick = { onclick() }) {
        Text(text)
    }
}

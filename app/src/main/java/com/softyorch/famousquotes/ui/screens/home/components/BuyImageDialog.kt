package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.screens.home.components.BuyActions.BuyImage
import com.softyorch.famousquotes.ui.screens.home.components.BuyActions.Exit
import com.softyorch.famousquotes.ui.screens.home.components.BuyActions.ViewAdd
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyImageDialog(
    title: String,
    textBtnPositive: String,
    textBtnNegative: String,
    onActions: (BuyActions) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onActions(Exit) },
        modifier = Modifier.background(
            brush = brushBackGround(),
            shape = MaterialTheme.shapes.extraLarge
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(color = White))
                Spacer(modifier = Modifier.size(8.dp))
                Icon(imageVector = Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(32.dp), tint = SecondaryColor)
            }
            SpacerHeight(height = 8)

            ButtonBuyImageDialog(textBtnPositive, Icons.Rounded.SmartDisplay) { onActions(ViewAdd) }
            ButtonBuyImageDialog(textBtnNegative, Icons.Rounded.Paid) { onActions(BuyImage) }
        }
    }
}

@Composable
private fun ButtonBuyImageDialog(
    text: String,
    icon: ImageVector,
    onActions: () -> Unit,
) {
    Button(
        modifier = Modifier.defaultMinSize(minWidth = 250.dp),
        onClick = { onActions() },
        colors = ButtonDefaults.buttonColors(SecondaryColor),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

sealed interface BuyActions {
    data object ViewAdd : BuyActions
    data object BuyImage : BuyActions
    data object Exit : BuyActions
}

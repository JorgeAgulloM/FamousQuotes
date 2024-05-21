package com.softyorch.famousquotes.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.theme.brushBackGround

@Composable
fun InfoDialog(onAction: () -> Unit) {
    Dialog(onDismissRequest = { onAction() }) {
        Box(modifier = Modifier.background(WhiteSmoke, shape = MaterialTheme.shapes.extraLarge)
            .clearAndSetSemantics {
                testTag = stringResource(R.string.main_info_dialog_text_info)
            }
        ) {
            Column(
                modifier = Modifier.background(
                    brush = brushBackGround(),
                    shape = MaterialTheme.shapes.extraLarge
                ).padding(16.dp)
            ) {
                InfoIcons(
                    icon = Icons.Outlined.Info,
                    text = stringResource(R.string.main_info_dialog_text_info)
                )
                SpacerHeight(height = 32)
                InfoIcons(
                    icon = Icons.Outlined.LocalMall,
                    text = stringResource(R.string.main_info_dialog_text_buy_image)
                )
                SpacerHeight()
                InfoIcons(
                    icon = Icons.Outlined.RestartAlt,
                    text = stringResource(R.string.main_info_dialog_text_other_quote)
                )
                SpacerHeight()
                InfoIcons(
                    icon = Icons.Outlined.Share,
                    text = stringResource(R.string.main_info_dialog_text_)
                )
                SpacerHeight()
                InfoIcons(
                    icon = Icons.Outlined.Person,
                    text = stringResource(R.string.main_info_dialog_owner)
                )
                SpacerHeight()
                InfoIcons(
                    icon = Icons.Outlined.WifiOff,
                    tint = MaterialTheme.colorScheme.error,
                    text = stringResource(R.string.main_info_dialog_connection)
                )
                SpacerHeight()
                TextToClick(text = "V: ${BuildConfig.VERSION_NAME}")
            }
        }
    }
}

@Composable
fun SpacerHeight(height: Int = 16) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun InfoIcons(icon: ImageVector, tint: Color = PrimaryColor, text: String) {
    Row {
        Icon(imageVector = icon, contentDescription = text, tint = tint)
        TextInfo(text)
    }
}

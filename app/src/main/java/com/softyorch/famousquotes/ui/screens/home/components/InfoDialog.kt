package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.theme.brushBackGround

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDialog(onAction: () -> Unit) {
    val infoText = stringResource(R.string.main_info_dialog_text_info)
    BasicAlertDialog(onDismissRequest = { onAction() }, modifier = Modifier.semantics {
        text = AnnotatedString(text = infoText)
    }) {
        Box(modifier = Modifier.background(WhiteSmoke, shape = MaterialTheme.shapes.medium)) {
            Column(
                modifier = Modifier.background(
                    brush = brushBackGround(),
                    shape = MaterialTheme.shapes.medium
                ).padding(16.dp)
            ) {
                AppIcon()
                HeaderQuote()
                SpacerHeight(height = 32)
                InfoIcons(
                    icon = Icons.Outlined.Info,
                    text = infoText
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
                    text = stringResource(R.string.main_icon_content_desc_lost_connection)
                )
                SpacerHeight()
                TextToClick(text = "V: ${BuildConfig.VERSION_NAME}")
            }
        }
    }
}

@Composable
private fun InfoIcons(icon: ImageVector, tint: Color = PrimaryColor, text: String) {
    Row {
        Icon(imageVector = icon, contentDescription = text, tint = tint)
        TextInfo(text)
    }
}

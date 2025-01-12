package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString

@Composable
fun AppIcon() {
    val context = LocalContext.current

    val appNameValue = context.getResourceString(BuildConfig.APP_TITLE)
    val appName = if (appNameValue.contains("_"))
        stringResource(R.string.app_name)
    else appNameValue

    val firstName = appName.split(" ")[0].uppercase()
    val secondName = appName.split(" ")[1].uppercase()

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_quote),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = AppColorSchema.secondary
        )
        Column(verticalArrangement = Arrangement.SpaceAround) {
            TextInfoApp(text = firstName, 12, 4)
            TextInfoApp(text = secondName, 12, -4)
        }
    }
}

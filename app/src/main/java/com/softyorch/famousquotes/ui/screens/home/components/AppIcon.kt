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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.theme.SecondaryColor

@Composable
fun AppIcon() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_quote),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = SecondaryColor
        )
        Column(verticalArrangement = Arrangement.SpaceAround) {
            TextInfoApp(text = "FRASES", 12, 4)
            TextInfoApp(text = "HISTORICAS", 12, -4)
        }
    }
}
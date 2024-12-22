package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun MessageToUser(
    icon: ImageVector,
    msg: String,
    tint: Color = PrimaryColor,
    contentDescription: String? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row (
            modifier = Modifier
                .background(color = WhiteSmoke, shape = MaterialTheme.shapes.large)
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
            Text(text = msg, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

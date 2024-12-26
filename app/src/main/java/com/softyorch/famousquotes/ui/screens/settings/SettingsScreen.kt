package com.softyorch.famousquotes.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onBackNavigation: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            Row(modifier = modifier.fillMaxWidth()) {
                IconButtonMenu(
                    cDescription = "Back",
                    icon = Icons.AutoMirrored.Filled.ArrowBack
                ) { onBackNavigation() }

                Text(
                    text = "Settings",
                    modifier = modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = WhiteSmoke,
                    style = MaterialTheme.typography.displaySmall
                )

                SpacerIconButton()
            }
            Image(
                painter = painterResource(R.drawable.under_construction),
                contentDescription = "Settings",
                modifier = modifier.padding(16.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(5)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

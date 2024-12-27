package com.softyorch.famousquotes.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.domain.model.SettingsModel
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
    val paddingTop = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp() + 24.dp
    }

    LaunchedEffect(Unit) {
        viewModel.actions(SettingsActions.GetSettings())
    }

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = modifier
                    .padding(paddingTop)
                    .fillMaxWidth()
            ) {
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
        },
        containerColor = BackgroundColor
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)
        ) {
            SettingsCheckers(
                settings = settings,
                onActions = viewModel::actions
            )
        }
    }
}

@Composable
fun SettingsCheckers(
    modifier: Modifier = Modifier,
    settings: SettingsModel,
    onActions: (SettingsActions) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        SwitchSettings(
            isChecked = settings.autoDarkMode,
            isEnable = true,
            titleText = "Auto Dark Mode",
            descriptionText = "Set to use system Dark Mode"
        ) { onActions(SettingsActions.AutoDarkMode(autoDarkMode = it)) }

        SwitchSettings(
            isChecked = settings.darkMode,
            isEnable = !settings.autoDarkMode,
            titleText = "Dark Mode",
            descriptionText = "Set Dark Mode in app"
        ) { onActions(SettingsActions.DarkMode(darkMode = it)) }

        SwitchSettings(
            isChecked = settings.leftHanded,
            isEnable = true,
            titleText = "Left-handed",
            descriptionText = "Move controls to left site"
        ) { onActions(SettingsActions.LeftHanded(leftHanded = it)) }

        SwitchSettings(
            isChecked = settings.notificationChannel,
            isEnable = true,
            titleText = "Notification channel",
            descriptionText = "Subscribe to daily notifications"
        ) { onActions(SettingsActions.NotificationChannel(notificationChannel = it)) }
    }
}

package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.runtime.Composable
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick

@Composable
fun AppVersionText(isCenter: Boolean = true, onClick: () -> Unit = {}) {
    TextToClick(text = "V: ${BuildConfig.VERSION_NAME}", isCenter = isCenter, onclick = onClick)
}

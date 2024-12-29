package com.softyorch.famousquotes.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Bonified
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.core.navigation.NavigationWrapper
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme

@Composable
fun MainApp(modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel(), onApplyTheme: () -> Unit) {

    LaunchedEffect(Unit) { viewModel.getSettings() }

    // Start AdMob Ads
    Interstitial()
    Bonified()

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val darkTheme = when {
        !settings.autoDarkMode && settings.darkMode -> true
        settings.autoDarkMode -> isSystemInDarkTheme()
        else -> false
    }

    FamousQuotesTheme(darkTheme = darkTheme) {
        onApplyTheme()
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColorSchema.background),
            contentAlignment = Alignment.BottomCenter
        ) {
            NavigationWrapper(leftHanded = settings.leftHanded, darkTheme = darkTheme)
            Banner.bannerInstance.StartAdView()
        }
    }
}

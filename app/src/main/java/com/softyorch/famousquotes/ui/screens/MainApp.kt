package com.softyorch.famousquotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Bonified
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.core.navigation.NavigationWrapper
import com.softyorch.famousquotes.ui.theme.BackgroundColor

@Composable
fun MainApp(modifier: Modifier = Modifier) {

    // Start AdMob Ads
    Banner.bannerInstance.StartAdView()
    Interstitial()
    Bonified()

    Box(
        modifier = modifier.fillMaxSize().background(BackgroundColor),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationWrapper()
        Banner.bannerInstance.apply {
            StartAdView()
            Show()
        }
    }
}

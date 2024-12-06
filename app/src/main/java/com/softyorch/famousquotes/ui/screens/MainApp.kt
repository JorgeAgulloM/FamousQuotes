package com.softyorch.famousquotes.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.navigation.NavigationWrapper

@Composable
fun MainApp(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
        NavigationWrapper()
        Banner.bannerInstance.Show()
    }
}

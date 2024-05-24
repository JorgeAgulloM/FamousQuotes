package com.softyorch.famousquotes.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.navigation.NavigationRoutes
import com.softyorch.famousquotes.utils.appIcon
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navHost: NavHostController) {

    LaunchedEffect(true) {
        delay(2000L)

        navHost.navigate(NavigationRoutes.HomeScreen.route) {
            navHost.popBackStack(route = NavigationRoutes.SplashScreen.route, true)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = appIcon(),
            modifier = Modifier.clip(shape = CircleShape).size(180.dp),
            contentDescription = stringResource(R.string.main_content_desc_image)
        )
    }
}

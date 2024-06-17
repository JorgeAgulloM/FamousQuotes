package com.softyorch.famousquotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Bonified
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.screens.home.HomeScreen
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.ui.screens.splash.SplashScreen
import com.softyorch.famousquotes.utils.sdk32AndUp

@Composable
fun NavigationManager(navHost: NavHostController = rememberNavController()) {
    Banner.bannerInstance.StartAdView()
    Interstitial()
    Bonified()

    val homeViewModel = hiltViewModel<HomeViewModel>()

    val startDestination =
        sdk32AndUp { NavigationRoutes.HomeScreen.route } ?: NavigationRoutes.SplashScreen.route

    NavHost(navController = navHost, startDestination = startDestination) {
        composable(route = NavigationRoutes.SplashScreen.route) {
            SplashScreen(navHost = navHost)
        }
        composable(route = NavigationRoutes.HomeScreen.route) {
            HomeScreen(viewModel = homeViewModel)
        }
    }
}
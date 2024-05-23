package com.softyorch.famousquotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.screens.home.HomeScreen
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.ui.screens.splash.SplashScreen

@Composable
fun NavigationManager(navHost: NavHostController = rememberNavController()) {
    Banner.bannerInstance.startAdView()

    val homeViewModel = hiltViewModel<HomeViewModel>()

    NavHost(navController = navHost, startDestination = NavigationRoutes.SplashScreen.route) {
        composable(route = NavigationRoutes.SplashScreen.route) {
            SplashScreen(navHost = navHost)
        }
        composable(route = NavigationRoutes.HomeScreen.route) {
            HomeScreen(navHost = navHost, viewModel = homeViewModel)
        }
    }
}
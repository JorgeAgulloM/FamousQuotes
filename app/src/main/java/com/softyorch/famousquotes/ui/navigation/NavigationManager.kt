package com.softyorch.famousquotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softyorch.famousquotes.ui.admob.startAdView
import com.softyorch.famousquotes.ui.screens.home.HomeScreen
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.ui.screens.splash.SplashScreen

@Composable
fun NavigationManager(navHost: NavHostController = rememberNavController()) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val startAdView = startAdView()

    NavHost(navController = navHost, startDestination = NavigationRoutes.SplashScreen.route) {
        composable(route = NavigationRoutes.SplashScreen.route) {
            SplashScreen(navHost = navHost)
        }
        composable(route = NavigationRoutes.HomeScreen.route) {
            HomeScreen(navHost = navHost, viewModel = homeViewModel, startAdView = startAdView)
        }
    }
}
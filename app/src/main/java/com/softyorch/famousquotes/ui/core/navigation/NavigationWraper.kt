package com.softyorch.famousquotes.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softyorch.famousquotes.ui.screens.grid.GridScreen
import com.softyorch.famousquotes.ui.screens.grid.GridViewModel
import com.softyorch.famousquotes.ui.screens.home.HomeScreen
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.ui.screens.settings.SettingsScreen
import com.softyorch.famousquotes.ui.screens.settings.SettingsViewModel
import com.softyorch.famousquotes.ui.screens.splash.SplashScreen
import com.softyorch.famousquotes.utils.sdk32AndUp

@Composable
fun NavigationWrapper(navController: NavHostController = rememberNavController()) {

    val homeViewModel = hiltViewModel<HomeViewModel>()

    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
           sdk32AndUp {
               navController.navigate(Home)
           } ?: SplashScreen(navigateHome = {
               navController.navigate(Home) { popUpTo(Splash) { inclusive = true } }
           })
        }
        composable<Home> {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToUserScreen = { navController.navigate(Grid) },
                onNavigateToSettings = { navController.navigate(Settings)}
            )
        }
        composable<Grid> {
            val gridViewModel = hiltViewModel<GridViewModel>()
            GridScreen(viewModel = gridViewModel, navigateBack = { navController.navigateUp() })
        }
        composable<Settings> {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(viewModel = settingsViewModel, onBackNavigation = { navController.navigateUp() })
        }
    }
}
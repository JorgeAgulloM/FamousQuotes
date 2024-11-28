package com.softyorch.famousquotes.ui.core.navigation

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
import com.softyorch.famousquotes.ui.screens.user.UserViewModel
import com.softyorch.famousquotes.ui.screens.user.UserScreen
import com.softyorch.famousquotes.utils.sdk32AndUp

@Composable
fun NavigationWrapper(navController: NavHostController = rememberNavController()) {

    // Start AdMob Ads
    Banner.bannerInstance.StartAdView()
    Interstitial()
    Bonified()

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()

    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
           sdk32AndUp {
               navController.navigate(Home)
           } ?: SplashScreen(navigateHome = {
               navController.navigate(Home) { popUpTo(Splash) { inclusive = true } }
           })
        }
        composable<Home> {
            HomeScreen(viewModel = homeViewModel, onNavigateToUserScreen = { navController.navigate(User) })
        }
        composable<User> {
            UserScreen(viewModel = userViewModel, navigateBack = { navController.navigateUp() })
        }
    }
}
package com.softyorch.famousquotes.ui.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softyorch.famousquotes.ui.screens.detail.DetailActions
import com.softyorch.famousquotes.ui.screens.detail.DetailScreen
import com.softyorch.famousquotes.ui.screens.detail.DetailViewModel
import com.softyorch.famousquotes.ui.screens.grid.GridScreen
import com.softyorch.famousquotes.ui.screens.grid.GridViewModel
import com.softyorch.famousquotes.ui.screens.home.HomeScreen
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel.Companion.HTTP
import com.softyorch.famousquotes.ui.screens.info.InfoScreen
import com.softyorch.famousquotes.ui.screens.info.InfoViewModel
import com.softyorch.famousquotes.ui.screens.onboading.OnBoardingScreen
import com.softyorch.famousquotes.ui.screens.onboading.OnBoardingViewModel
import com.softyorch.famousquotes.ui.screens.settings.SettingsScreen
import com.softyorch.famousquotes.ui.screens.settings.SettingsViewModel
import com.softyorch.famousquotes.ui.screens.splash.SplashScreen
import com.softyorch.famousquotes.utils.sdk32AndUp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationWrapper(
    navController: NavHostController = rememberNavController(),
    leftHanded: Boolean,
    darkTheme: Boolean,
    isShowOnBoarding: Boolean
) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val gridViewModel = hiltViewModel<GridViewModel>()
    val detailViewModel = hiltViewModel<DetailViewModel>()

    var navigateToDetail by remember { mutableStateOf(false) }

    val detailGetQuote by detailViewModel.quoteModel.collectAsStateWithLifecycle()
    if (detailGetQuote.imageUrl.startsWith(HTTP) && navigateToDetail) {
        navigateToDetail = false
        navController.navigate(Detail(detailGetQuote.id))
    }

    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Splash) {

            composable<Splash> {
                val toNext: Any = if (!isShowOnBoarding) OnBoarding(true) else Home

                sdk32AndUp {
                    navController.navigate(toNext) {
                        popUpTo(Splash) { inclusive = true }
                    }
                } ?: SplashScreen(navigateHome = {
                    navController.navigate(toNext) {
                        popUpTo(Splash) { inclusive = true }
                    }
                })
            }
            composable<Home> {
                HomeScreen(
                    viewModel = homeViewModel,
                    leftHanded = leftHanded,
                    onNavigateToUserScreen = { navController.navigate(Grid) },
                    onNavigateToSettings = { navController.navigate(Settings) }
                )
            }
            composable<Grid> {
                GridScreen(
                    viewModel = gridViewModel,
                    leftHanded = leftHanded,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onNavigateToDetail = { idQuote ->
                        navigateToDetail = true
                        detailViewModel.setDetailAction(DetailActions.ExitDetail(), idQuote)
                        detailViewModel.setDetailAction(DetailActions.LoadQuoteData(), idQuote)
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable<Detail> {
                val id = it.arguments?.getString("id") ?: ""
                DetailScreen(
                    viewModel = detailViewModel,
                    id = id,
                    leftHanded = leftHanded,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                ) {
                    navController.navigateUp()
                }
            }
            composable<Settings> {
                val settingsViewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToOnBoarding = { navController.navigate(OnBoarding()) },
                    onNavigateToInfo = { navController.navigate(Info) },
                    onBackNavigation = { navController.navigateUp() })
            }
            composable<OnBoarding> {
                val gotoHome = it.arguments?.getBoolean("goToHome") ?: false
                val viewModel = hiltViewModel<OnBoardingViewModel>()
                OnBoardingScreen(viewModel = viewModel, leftHanded = leftHanded) {
                    if (gotoHome) navController.navigate(Home)
                    else navController.navigateUp()
                }
            }
            composable<Info> {
                val viewModel = hiltViewModel<InfoViewModel>()
                InfoScreen(viewModel = viewModel, leftHanded = leftHanded, darkTheme = darkTheme) {
                    navController.navigateUp()
                }
            }
        }
    }
}

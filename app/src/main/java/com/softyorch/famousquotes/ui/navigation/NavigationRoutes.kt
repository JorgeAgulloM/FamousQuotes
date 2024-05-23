package com.softyorch.famousquotes.ui.navigation

sealed class NavigationRoutes(val route: String) {
    enum class Routes { SPLASH, HOME }

    data object SplashScreen: NavigationRoutes(route = Routes.SPLASH.name)
    data object HomeScreen: NavigationRoutes(route = Routes.HOME.name)
}

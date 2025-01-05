package com.softyorch.famousquotes.ui.screens.info

sealed interface InfoActions {
    data object Support : InfoActions
    data object BuyMeACoffee : InfoActions
    data object CoffeeWithPayPal : InfoActions
    data object OpenGooglePlay : InfoActions
}

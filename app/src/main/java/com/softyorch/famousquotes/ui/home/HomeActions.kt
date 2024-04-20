package com.softyorch.famousquotes.ui.home

sealed class HomeActions {
    data object Info: HomeActions()
    data object New: HomeActions()
    data object Send: HomeActions()
    data object Buy: HomeActions()
}

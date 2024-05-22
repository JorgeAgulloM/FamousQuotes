package com.softyorch.famousquotes.ui.home

sealed class HomeActions {
    data object Info: HomeActions()
    data object New: HomeActions()
    data object Send: HomeActions()
    data object Buy: HomeActions()
    data object Owner: HomeActions()
    data object Like: HomeActions()
    data object ShowImage: HomeActions()
    data object ShowNoConnectionDialog: HomeActions()
    data object ReConnection: HomeActions()
}

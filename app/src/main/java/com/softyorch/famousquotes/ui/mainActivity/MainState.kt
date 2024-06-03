package com.softyorch.famousquotes.ui.mainActivity

sealed class MainState {
    data object TimeToUpdate: MainState()
    data object Home: MainState()
    data object Unauthorized: MainState()
}

package com.softyorch.famousquotes.ui.mainActivity

sealed class MainState {
    data object TimeToUpdate: MainState()
    data object Loading: MainState()
    data object Home: MainState()
}

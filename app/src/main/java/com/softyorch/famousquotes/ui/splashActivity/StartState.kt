package com.softyorch.famousquotes.ui.splashActivity

sealed class StartState {
    data object Loading: StartState()
    data object Start: StartState()
}
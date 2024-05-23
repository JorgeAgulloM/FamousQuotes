package com.softyorch.famousquotes.ui.splashActivity

sealed class AlertState {
    data object Update: AlertState()
    data object Dismiss: AlertState()
}

package com.softyorch.famousquotes.ui.mainActivity

sealed class AlertState {
    data object Update: AlertState()
    data object Dismiss: AlertState()
}

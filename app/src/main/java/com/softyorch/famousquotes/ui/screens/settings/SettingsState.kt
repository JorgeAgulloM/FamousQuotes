package com.softyorch.famousquotes.ui.screens.settings

data class SettingsState(
    val isLoading: Boolean = false,
    val errorSubscribe: Boolean = false,
    val notificationsPermissionState: NotificationsPermissionState = NotificationsPermissionState.Granted
)

sealed interface NotificationsPermissionState {
    data object Granted : NotificationsPermissionState
    data object Denied : NotificationsPermissionState
    data object Waiting : NotificationsPermissionState
    data object Blocked : NotificationsPermissionState
    data object BlockedConfirmed : NotificationsPermissionState
}

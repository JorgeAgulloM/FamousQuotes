package com.softyorch.famousquotes.ui.screens.settings

sealed interface SettingsActions {
    data class GetSettings(val action: String = "GetSettings"): SettingsActions
    data class AutoDarkMode(val action: String = "AutoDarkMode", val autoDarkMode: Boolean): SettingsActions
    data class DarkMode(val action: String = "DarkMode", val darkMode: Boolean): SettingsActions
    data class LeftHanded(val action: String = "LeftHanded", val leftHanded: Boolean): SettingsActions
    data class NotificationChannel(val action: String = "NotificationChannel", val notificationChannel: Boolean): SettingsActions
    data class NotificationsPermissionRequest(val action: String = "NotificationsPermissionRequest"): SettingsActions
    data class NotificationsPermissionUserWantBlock(val action: String = "NotificationsPermissionUserWantBlock"): SettingsActions
    data class NotificationsPermissionUserBlocked(val action: String = "NotificationsPermissionUserBlocked"): SettingsActions
    data class IsShowOnBoarding(val action: String = "IsShowOnBoarding", val isShowOnBoarding: Boolean): SettingsActions
}
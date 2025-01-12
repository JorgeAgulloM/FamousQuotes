package com.softyorch.famousquotes.data.datastore.model

data class SettingsStoreModel(
    val autoDarkMode: Boolean,
    val darkMode: Boolean,
    val leftHanded: Boolean,
    val notificationChannel: Boolean,
    val isShowOnBoarding: Boolean
) {
    companion object {
        const val AUTO_DARK_MODE = "auto_dark_mode"
        const val DARK_MODE = "dark_mode"
        const val LEFT_HANDED = "left_handed"
        const val NOTIFICATION_CHANNEL = "notification_channel"
        const val IS_SHOW_ON_BOARDING = "is_show_on_boarding"
    }
}

package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel

data class SettingsModel(
    val autoDarkMode: Boolean,
    val darkMode: Boolean,
    val leftHanded: Boolean,
    val notificationChannel: Boolean,
    val isShowOnBoarding: Boolean
) {
    companion object {
        val DEFAULT = SettingsModel(
            autoDarkMode = false,
            darkMode = false,
            leftHanded = false,
            notificationChannel = false,
            isShowOnBoarding = false
        )

        fun SettingsStoreModel.toSettingsModel() = SettingsModel(
            autoDarkMode = autoDarkMode,
            darkMode = darkMode,
            leftHanded = leftHanded,
            notificationChannel = notificationChannel,
            isShowOnBoarding = isShowOnBoarding
        )

        fun SettingsModel.toSettingsStoreModel() = SettingsStoreModel(
            autoDarkMode = autoDarkMode,
            darkMode = darkMode,
            leftHanded = leftHanded,
            notificationChannel = notificationChannel,
            isShowOnBoarding = isShowOnBoarding
        )
    }
}

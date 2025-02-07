package com.softyorch.famousquotes.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.core.NotificationUtils
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.model.SubscribeNotificationDTO
import com.softyorch.famousquotes.domain.useCases.notificationSubscribe.SubscribeNotificationByTopic
import com.softyorch.famousquotes.domain.useCases.settings.GetSettings
import com.softyorch.famousquotes.domain.useCases.settings.SetSettings
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.utils.notificationChannelByUserLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettings: GetSettings,
    private val setSettings: SetSettings,
    private val subscribeNotificationsByTopic: SubscribeNotificationByTopic,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsModel.DEFAULT)
    val settings: StateFlow<SettingsModel> = _settings

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    fun actions(actions: SettingsActions) {
        Analytics.sendAction(Analytics.ActionSettings(actions))
        when (actions) {
            is SettingsActions.GetSettings -> getSettings()
            is SettingsActions.AutoDarkMode -> setAutoDarkMode(actions.autoDarkMode)
            is SettingsActions.DarkMode -> setManualDarkMode(actions.darkMode)
            is SettingsActions.LeftHanded -> setLeftHanded(actions.leftHanded)
            is SettingsActions.NotificationChannel -> verifyNotificationsPermission(actions.notificationChannel)
            is SettingsActions.NotificationsPermissionRequest -> verifyNotificationsPermissionRequest()
            is SettingsActions.NotificationsPermissionUserWantBlock -> userWantBlockedNotifications()
            is SettingsActions.NotificationsPermissionUserBlocked -> userConfirmedBlockedNotifications()
            is SettingsActions.IsShowOnBoarding -> setIsShowOnBoarding(actions.isShowOnBoarding)
        }
    }

    private fun getSettings() {
        viewModelScope.launch(dispatcherDefault) {
            getSettings.invoke().collect { settings ->
                _settings.update { settings }.also {
                    if (settings.notificationChannel) verifyActivatedNotifications()
                }
            }
        }
    }

    private fun setAutoDarkMode(newValue: Boolean) {
        viewModelScope.launch {
            setSettingsChanges(_settings.value.copy(autoDarkMode = newValue))
        }
    }

    private fun setManualDarkMode(newValue: Boolean) {
        viewModelScope.launch {
            setSettingsChanges(_settings.value.copy(darkMode = newValue))
        }
    }

    private fun setLeftHanded(newValue: Boolean) {
        viewModelScope.launch {
            setSettingsChanges(_settings.value.copy(leftHanded = newValue))
        }
    }

    private fun verifyActivatedNotifications() {
        viewModelScope.launch {
            NotificationUtils(MainActivity.instance)
                .verifyPermissionNotifications(request = false) { statePermission ->
                    setSettingsChanges(_settings.value.copy(notificationChannel = statePermission))
                }
        }
    }

    private fun verifyNotificationsPermission(newValue: Boolean) {
        _state.update { it.copy(isLoading = true) }

        if (newValue) NotificationUtils(MainActivity.instance)
            .verifyPermissionNotifications(request = false) { statePermission ->
                if (!statePermission)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notificationsPermissionState = NotificationsPermissionState.Denied
                        )
                    }
                else {
                    _state.update {
                        it.copy(notificationsPermissionState = NotificationsPermissionState.Granted)
                    }
                    setNotificationsChannel(newValue)
                }
            } else setNotificationsChannel(newValue)
    }

    private fun verifyNotificationsPermissionRequest() {
        NotificationUtils(MainActivity.instance).goToConfigurationNotifications()
        _state.update {
            it.copy(
                //Prueba el loading
                notificationsPermissionState = NotificationsPermissionState.Waiting
            )
        }
    }

    private fun userWantBlockedNotifications() {
        _state.update {
            it.copy(notificationsPermissionState = NotificationsPermissionState.Blocked)
        }
    }

    private fun userConfirmedBlockedNotifications() {
        _state.update {
            it.copy(notificationsPermissionState = NotificationsPermissionState.BlockedConfirmed)
        }
    }

    private fun setNotificationsChannel(newValue: Boolean) {
        viewModelScope.launch {
            val notificationChannel = notificationChannelByUserLanguage()
            val result = withContext(dispatcherDefault) {
                subscribeNotificationsByTopic.invoke(
                    SubscribeNotificationDTO(
                        subscribe = newValue,
                        topic = notificationChannel
                    )
                )
            }

            if (result.subscribe)
                setSettingsChanges(_settings.value.copy(notificationChannel = newValue))

            _state.update { it.copy(isLoading = false, errorSubscribe = !result.subscribe) }
        }
    }

    private fun setIsShowOnBoarding(newValue: Boolean) {
        viewModelScope.launch {
            setSettingsChanges(_settings.value.copy(isShowOnBoarding = newValue))
        }
    }

    private fun setSettingsChanges(settings: SettingsModel) {
        viewModelScope.launch(dispatcherDefault) {
            setSettings.invoke(settings)
        }
    }

}

package com.softyorch.famousquotes.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.FIREBASE_NOTIFICATION_CHANNEL_1
import com.softyorch.famousquotes.domain.model.SubscribeNotificationDTO
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.useCases.notificationSubscribe.SubscribeNotificationByTopic
import com.softyorch.famousquotes.domain.useCases.settings.GetSettings
import com.softyorch.famousquotes.domain.useCases.settings.SetSettings
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
        when (actions) {
            is SettingsActions.GetSettings -> getSettings()
            is SettingsActions.AutoDarkMode -> setAutoDarkMode(actions.autoDarkMode)
            is SettingsActions.DarkMode -> setManualDarkMode(actions.darkMode)
            is SettingsActions.LeftHanded -> setLeftHanded(actions.leftHanded)
            is SettingsActions.NotificationChannel -> setNotificationChannel(actions.notificationChannel)
            is SettingsActions.IsShowOnBoarding -> setIsShowOnBoarding(actions.isShowOnBoarding)
        }
    }

    private fun getSettings() {
        viewModelScope.launch(dispatcherDefault) {
            getSettings.invoke().collect { settings ->
                _settings.update { settings }
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

    private fun setNotificationChannel(newValue: Boolean) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = withContext(dispatcherDefault) {
                subscribeNotificationsByTopic.invoke(
                    SubscribeNotificationDTO(
                        subscribe = newValue,
                        topic = FIREBASE_NOTIFICATION_CHANNEL_1
                    )
                )
            }

            if (result.subscribe)
                setSettingsChanges(_settings.value.copy(notificationChannel = newValue))

            val getError = newValue != result.subscribe
            _state.update { it.copy(isLoading = false, errorSubscribe = getError) }
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

package com.softyorch.famousquotes.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.useCases.settings.GetSettings
import com.softyorch.famousquotes.domain.useCases.settings.SetSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettings: GetSettings,
    private val setSettings: SetSettings,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsModel.DEFAULT)
    val settings: StateFlow<SettingsModel> = _settings

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
        viewModelScope.launch {
            setSettingsChanges(_settings.value.copy(notificationChannel = newValue))
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

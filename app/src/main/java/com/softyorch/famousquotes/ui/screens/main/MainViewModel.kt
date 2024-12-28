package com.softyorch.famousquotes.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.useCases.settings.GetSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSettings: GetSettings,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default
): ViewModel() {

    private val _settings = MutableStateFlow(SettingsModel.DEFAULT)
    val settings: StateFlow<SettingsModel> = _settings

    fun getSettings() {
        viewModelScope.launch(dispatcherDefault) {
            getSettings.invoke().collect { settings ->
                _settings.update { settings }
            }
        }
    }
}
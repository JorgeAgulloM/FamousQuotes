package com.softyorch.famousquotes.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.BuildConfig
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.domain.useCases.AuthConnection
import com.softyorch.famousquotes.domain.useCases.TimeToUpdate
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
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
class MainViewModel @Inject constructor(
    private val authService: AuthConnection,
    private val timeToUpdate: TimeToUpdate,
    private val intents: Intents,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainState>(MainState.Unauthorized)
    val mainState: StateFlow<MainState> = _uiState

    init {
        anonymousAuthentication()
        isTimeToUpdate()
    }

    fun goToUpdateApp() {
        viewModelScope.launch {
            intents.goToUpdateInGooglePlay()
        }
    }

    private fun isTimeToUpdate() {
        if (!BuildConfig.DEBUG) viewModelScope.launch {
            val needUpdateApp = withContext(dispatcherIO) {
                timeToUpdate()
            }
            writeLog(LevelLog.INFO, "Is time to Update?: $needUpdateApp")
            if (needUpdateApp) _uiState.update { MainState.TimeToUpdate }
        }
    }

    private fun anonymousAuthentication() {
        viewModelScope.launch {
            withContext(dispatcherIO) {
                authService()
            }.also {
                if (it) {
                    _uiState.update { MainState.Home }
                    writeLog(LevelLog.INFO, "Anonymous Authentication is Success!!")
                } else {
                    anonymousAuthentication()
                    writeLog(LevelLog.ERROR, "Error: Anonymous Authentication is Failed")
                }
            }
        }
    }
}

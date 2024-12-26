package com.softyorch.famousquotes.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.useCases.AuthConnection
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
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainState>(MainState.Start)
    val mainState: StateFlow<MainState> = _uiState

    init {
        anonymousAuthentication()
    }

    private fun anonymousAuthentication() {
        viewModelScope.launch {
            withContext(dispatcherDefault) {
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

package com.softyorch.famousquotes.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
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
    private val timeToUpdate: TimeToUpdate,
    private val intents: Intents,
    private val selectQuote: GetTodayQuote,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
): ViewModel() {
    private val _uiState = MutableStateFlow<MainState>(MainState.Loading)
    val mainState: StateFlow<MainState> = _uiState

    init {
        isTimeToUpdate()
    }

    fun goToUpdateApp() {
        viewModelScope.launch {
            intents.goToUpdateInGooglePlay()
        }
    }

    private fun isTimeToUpdate() {
        viewModelScope.launch {
            val needUpdateApp = withContext(dispatcherIO) {
                timeToUpdate()
            }
            writeLog(LevelLog.INFO, "Is time to Update?: $needUpdateApp")
            _uiState.update {
                startGettingQuote()
                if (needUpdateApp) {
                    MainState.TimeToUpdate
                } else {
                    MainState.Home
                }
            }
        }
    }

    private fun startGettingQuote() {
        viewModelScope.launch {
            withContext(dispatcherIO) {
                try {
                    selectQuote()
                } catch (ex: Exception) {
                    writeLog(LevelLog.ERROR, "Error starting: ${ex.cause}")
                }
            }

            _uiState.update { MainState.Home }
        }
    }
}
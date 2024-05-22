package com.softyorch.famousquotes.ui.splashActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
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
class StartViewModel @Inject constructor(
    private val selectQuote: GetTodayQuote,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
): ViewModel() {

    private val _uiState = MutableStateFlow<StartState>(StartState.Loading)
    val uiState: StateFlow<StartState> = _uiState

    init {
        startGettingQuote()
    }

    private fun startGettingQuote() {
        viewModelScope.launch {
            withContext(dispatcherIO) {
                selectQuote()
            }

            _uiState.update { StartState.Start }
        }
    }
}
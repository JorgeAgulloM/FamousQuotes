package com.softyorch.famousquotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.SelectRandomQuote
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
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
class HomeViewModel @Inject constructor(
    private val selectQuote: SelectRandomQuote,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel("", "", "")))
    val uiState: StateFlow<HomeState> = _uiState

    init {
        getQuote()
    }

    private fun getQuote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val quote = withContext(dispatcherIO) {
                selectQuote()
            }
            if (quote != null)
                _uiState.update { it.copy(isLoading = false, quote = quote) }
        }
    }
}

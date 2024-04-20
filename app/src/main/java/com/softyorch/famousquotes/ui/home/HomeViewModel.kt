package com.softyorch.famousquotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.Send
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
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val send: Send,
    private val intents: Intents
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel("", "", "")))
    val uiState: StateFlow<HomeState> = _uiState

    init {
        getQuote()
    }

    fun onActions(action: HomeActions) {
        when (action) {
            HomeActions.Info -> { showInfoDialog() }
            HomeActions.New -> { loadNewRandomQuote() }
            HomeActions.Send -> { shareQuote() }
            HomeActions.Buy -> { goToBuyImage() }
        }
    }

    private fun showInfoDialog() {
        _uiState.update { it.copy(showInfo = !it.showInfo) }
    }

    private fun loadNewRandomQuote() {
        if (!_uiState.value.showInterstitial) {
            _uiState.update { it.copy(showInterstitial = true) }
            getRandomQuote()
        } else _uiState.update { it.copy(showInterstitial = false) }
    }

    private fun shareQuote() {
        val dataToSend = "${_uiState.value.quote.body} - ${_uiState.value.quote.owner}"
        send.sendDataTo(dataToSend)
    }

    private fun goToBuyImage() {
        viewModelScope.launch {
            intents.goToWebShopImages()
        }
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

    private fun getRandomQuote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val quote = withContext(dispatcherIO) {
                selectQuote.getRandomQuote()
            }
            if (quote != null)
                _uiState.update { it.copy(isLoading = false, quote = quote) }
        }
    }
}

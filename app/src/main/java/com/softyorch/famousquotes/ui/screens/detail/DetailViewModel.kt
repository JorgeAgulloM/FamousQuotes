package com.softyorch.famousquotes.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetQuoteById
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
class DetailViewModel @Inject constructor(
    private val getQuoteById: GetQuoteById,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ViewModel() {

    private val _quote = MutableStateFlow(FamousQuoteModel.emptyModel())
    val quote: StateFlow<FamousQuoteModel> = _quote

    fun getQuote(id: String) {
        viewModelScope.launch {
            val result = withContext(defaultDispatcher) {
                getQuoteById(id)
            }
            result?.let { quoteResult -> _quote.update { quoteResult } }
        }
    }

}
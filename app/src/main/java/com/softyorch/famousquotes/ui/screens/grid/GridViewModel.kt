package com.softyorch.famousquotes.ui.screens.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetAllQuotesFiltered
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    private val getQuotes: GetAllQuotesFiltered,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _quotes = MutableStateFlow<List<FamousQuoteModel>>(emptyList())
    val quotes: StateFlow<List<FamousQuoteModel>> = _quotes

    private val _filterQuotesSelected = MutableStateFlow<FilterQuotes>(FilterQuotes.Likes)
    val filterQuotesSelected: StateFlow<FilterQuotes> = _filterQuotesSelected

    private val _state = MutableStateFlow(GridState())
    val state: StateFlow<GridState> = _state

    fun setAction(action: GridActions) {
        when (action) {
            is GridActions.LoadingQuotes -> getAllQuotes()
            is GridActions.AscendingOrder -> revertOrder()
            is GridActions.DescendingOrder -> revertOrder()
        }
    }

    private fun getAllQuotes() {
        _state.update { it.copy(isLoading = true) }
        getFilteredQuotes()
    }

    fun selectFilterQuotes(filterQuotes: FilterQuotes) {
        _filterQuotesSelected.update { filterQuotes }
        getFilteredQuotes()
    }

    private fun getFilteredQuotes() {
        val isAscending: Boolean = _state.value.orderByAscending
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatcherDefault) {
            getQuotes.invoke(_filterQuotesSelected.value).collect { quotes ->
                quotes?.let { quoteList ->
                    _state.update { it.copy(isLoading = false) }
                    _quotes.update {
                        if (isAscending)
                            quoteList.map { quote -> quote ?: FamousQuoteModel.emptyModel() }
                        else
                            quoteList.sortedByDescending { quote ->
                                quote?.id
                            }.map { quote ->
                                quote ?: FamousQuoteModel.emptyModel()
                            }
                    }.also {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun revertOrder() {
        _state.update {
            it.copy(orderByAscending = !_state.value.orderByAscending)
        }
    }
}

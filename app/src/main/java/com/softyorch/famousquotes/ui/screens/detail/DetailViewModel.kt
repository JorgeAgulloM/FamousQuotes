package com.softyorch.famousquotes.ui.screens.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Star
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetQuoteById
import com.softyorch.famousquotes.ui.screens.detail.model.PropertyStatisticsModel
import com.softyorch.famousquotes.domain.useCases.GetStatistics
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
    private val getStatistics: GetStatistics,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ViewModel() {

    private val _quote = MutableStateFlow(FamousQuoteModel.emptyModel())
    val quote: StateFlow<FamousQuoteModel> = _quote

    private val _propertyList = MutableStateFlow(listOf<PropertyStatisticsModel>())
    val propertyList: StateFlow<List<PropertyStatisticsModel>> = _propertyList

    fun getQuote(id: String) {
        viewModelScope.launch {
            val result = withContext(defaultDispatcher) {
                getQuoteById(id)
            }
            result?.let { quoteResult -> _quote.update { quoteResult } }
        }
    }

    fun getStatistics(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            getStatistics.invoke(id).collect { properties ->
                _propertyList.update {
                    listOf(
                        PropertyStatisticsModel(
                            name = "Likes",
                            value = properties.likes,
                            icon = Icons.Outlined.FavoriteBorder
                        ),
                        PropertyStatisticsModel(
                            name = "Shown",
                            value = properties.shown,
                            icon = Icons.Outlined.RemoveRedEye
                        ),
                        PropertyStatisticsModel(
                            name = "Favorites",
                            value = properties.favorites,
                            icon = Icons.Outlined.Star
                        )
                    )
                }
            }
        }
    }

}
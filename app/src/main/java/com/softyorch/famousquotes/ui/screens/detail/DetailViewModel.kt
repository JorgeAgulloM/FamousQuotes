package com.softyorch.famousquotes.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.useCases.GetQuoteStatistics
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.GetUserFavoriteQuote
import com.softyorch.famousquotes.domain.useCases.SetQuoteFavorite
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetUserLikeQuote
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getQuote: GetTodayQuote,
    private val getUserLikeQuote: GetUserLikeQuote,
    private val getUserFavoriteQuote: GetUserFavoriteQuote,
    private val getQuoteStatistics: GetQuoteStatistics,
    private val setLike: SetQuoteLike,
    private val setFavorite: SetQuoteFavorite,
    private val shareQuote: ISend,
    private val storage: IStorageService,
    private val intents: Intents,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _quoteModel = MutableStateFlow(QuoteDetailsModel())
    val quoteModel: StateFlow<QuoteDetailsModel> = _quoteModel

    private val _detailActions = MutableStateFlow(DetailActions.LoadQuoteData())
    val detailActions: StateFlow<DetailActions> = _detailActions

    fun setDetailAction(action: DetailActions, id: String) {
        when (action) {
            is DetailActions.LoadQuoteData -> getData(id)
            is DetailActions.SetLikeQuote -> setLikeQuote(id)
            is DetailActions.SetFavoriteQuote -> setFavoriteQuote(id)
            is DetailActions.DownloadQuote -> downloadQuote(id)
            is DetailActions.ShareQuoteAsText -> sharedQuoteAsText(id)
            is DetailActions.ShareQuoteAsImage -> shareQuoteAsImage(id)
        }
    }

    private fun getData(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            combine(
                getQuote.invoke(id),
                getUserLikeQuote.invoke(id),
                getUserFavoriteQuote.invoke(id),
                getQuoteStatistics.invoke(id)
            ) { quoteResponse, isLikeResponse, isFavoriteResponse, statisticsResponse ->

                quoteResponse?.let { response ->
                    _quoteModel.update {
                        it.copy(
                            id = response.id,
                            body = response.body,
                            imageUrl = response.imageUrl,
                            owner = response.owner
                        )
                    }
                }

                isLikeResponse?.let { quoteLike ->
                    _quoteModel.update { it.copy(isLiked = quoteLike) }
                }

                isFavoriteResponse?.let { quoteFavorite ->
                    _quoteModel.update { it.copy(isFavorite = quoteFavorite) }
                }

                statisticsResponse?.let { quoteStatistics ->
                    _quoteModel.update {
                        it.copy(
                            likes = quoteStatistics.likes,
                            showns = quoteStatistics.showns,
                            favorites = quoteStatistics.favorites
                        )
                    }
                }
            }.collect {
                writeLog(LevelLog.INFO, "${this.javaClass.simpleName}: get data quote: ${_quoteModel.value}")
            }
        }
    }

    private fun setLikeQuote(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            val isLike = !_quoteModel.value.isLiked
            val updateLikes = LikesUiDTO(id = id, isLike = isLike)
            setLike(updateLikes.toDomain())
        }
    }

    private fun setFavoriteQuote(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            val isFavorite = !_quoteModel.value.isFavorite
            val favoriteState = FavoritesUiDTO(id = id, isFavorite = isFavorite)
            setFavorite(favoriteState.toDomain())
        }
    }

    private fun downloadQuote(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            storage.downloadImage(id) { result ->

            }
        }
    }

    private fun shareQuoteAsImage(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            shareQuote.shareImageTo(id, _quoteModel.value.imageUrl)
        }
    }

    private fun sharedQuoteAsText(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            shareQuote.shareTextTo(id)
        }
    }
}

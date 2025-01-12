package com.softyorch.famousquotes.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.domain.useCases.GetQuoteStatistics
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.GetUserFavoriteQuote
import com.softyorch.famousquotes.domain.useCases.SetQuoteFavorite
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetUserLikeQuote
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
    private val hasConnection: InternetConnection,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private var quoteJob: Job? = null
    private val _quoteModel = MutableStateFlow(QuoteDetailsModel())
    val quoteModel: StateFlow<QuoteDetailsModel> = _quoteModel

    private var statisticsJob: Job? = null
    private val _statisticsModel = MutableStateFlow(QuoteStatistics())
    val statisticsModel: StateFlow<QuoteStatistics> = _statisticsModel

    private val _detailState = MutableStateFlow(DetailState())
    val detailState: StateFlow<DetailState> = _detailState

    fun setDetailAction(action: DetailActions, id: String) {
        Analytics.sendAction(Analytics.ActionDetails(action))
        when (action) {
            is DetailActions.LoadQuoteData -> {
                getData(id)
                hasConnectionFlow()
            }
            is DetailActions.SetLikeQuote -> setLikeQuote(id)
            is DetailActions.SetFavoriteQuote -> setFavoriteQuote(id)
            is DetailActions.DownloadQuote -> downloadQuote(id)
            is DetailActions.HowToShareQuote -> howToShareQuote()
            is DetailActions.ShareQuoteAs -> askHowToShare(action.shareAs)
            is DetailActions.OwnerQuoteIntent -> goToOwnerInfo()
            is DetailActions.ShowNoConnectionDialog -> showNoConnectionDialog()
            is DetailActions.HideControls -> showHidControls()
            is DetailActions.ExitDetail -> deleteQuote()
        }
    }

    private fun getData(id: String) {
        quoteJob?.cancel()

        quoteJob = viewModelScope.launch(dispatcherDefault) {
            combine(
                getQuote.invoke(id),
                getUserLikeQuote.invoke(id),
                getUserFavoriteQuote.invoke(id)
            ) { quoteResponse, isLikeResponse, isFavoriteResponse ->

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
            }.collect {
                writeLog(LevelLog.INFO, "${this.javaClass.simpleName}: get data quote: ${_quoteModel.value}")
            }
        }
        getStatisticsQuote(id)
    }

    private fun getStatisticsQuote(id: String) {
        statisticsJob?.cancel()

        statisticsJob = viewModelScope.launch(dispatcherDefault) {
            getQuoteStatistics.invoke(id).collect { statisticsResponse ->
                statisticsResponse?.let { quoteStatistics ->
                    _statisticsModel.update {
                        it.copy(
                            likes = quoteStatistics.likes,
                            showns = quoteStatistics.showns,
                            favorites = quoteStatistics.favorites
                        )
                    }
                }
            }
        }
    }

    private fun hasConnectionFlow() {
        viewModelScope.launch(dispatcherDefault) {
            hasConnection.isConnectedFlow().catch {
                writeLog(ERROR, "Error getting connection state: ${it.cause}", it)
            }.collect { connection ->
                _detailState.update {
                    it.copy(
                        hasConnection = connection,
                        showDialogNoConnection = !connection
                    )
                }
            }
        }
    }

    private fun setLikeQuote(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            val isLike = !_quoteModel.value.isLiked
            val updateLikes = LikesUiDTO(id = id, isLike = isLike)
            setLike(updateLikes.toDomain())
        }
    }

    private fun setFavoriteQuote(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            val isFavorite = !_quoteModel.value.isFavorite
            val favoriteState = FavoritesUiDTO(id = id, isFavorite = isFavorite)
            setFavorite(favoriteState.toDomain())
        }
    }

    private fun downloadQuote(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            storage.downloadImage(id) {
                setLoadingState(false)
            }
        }
    }

    private fun howToShareQuote() {
        _detailState.update { it.copy(shareAs = !it.shareAs) }
    }

    private fun askHowToShare(shareAs: ShareAs) {
        _detailState.update { it.copy(shareAs = false) }
        val dataToSend = "${_quoteModel.value.body}\n'${_quoteModel.value.owner}'"

        setLoadingState(true)
        when (shareAs) {
            ShareAs.Text -> sharedQuoteAsText(dataToSend)
            ShareAs.Image -> shareQuoteAsImage(dataToSend)
        }
    }

    private fun shareQuoteAsImage(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            shareQuote.shareImageTo(id, _quoteModel.value.imageUrl)
            setLoadingState(false)
        }
    }

    private fun sharedQuoteAsText(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            shareQuote.shareTextTo(id)
            setLoadingState(false)
        }
    }

    private fun goToOwnerInfo() {
        viewModelScope.launch(dispatcherDefault) {
            intents.goToSearchOwnerInBrowser(_quoteModel.value.owner)
        }
    }

    private fun showNoConnectionDialog() {
        _detailState.update { it.copy(showDialogNoConnection = !it.showDialogNoConnection) }
    }

    private fun showHidControls() {
        _detailState.update { it.copy(hideControls = !it.hideControls) }
    }

    private fun deleteQuote() {
        _quoteModel.update { QuoteDetailsModel() }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _detailState.update { it.copy(isLoading = isLoading) }
    }
}

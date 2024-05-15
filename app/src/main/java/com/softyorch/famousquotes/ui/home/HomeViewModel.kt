package com.softyorch.famousquotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.core.Send
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikes
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.home.model.LikesUiDTO
import com.softyorch.famousquotes.ui.home.model.LikesUiDTO.Companion.toDomain
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
class HomeViewModel @Inject constructor(
    private val selectQuote: GetTodayQuote,
    private val getLikes: GetQuoteLikes,
    private val setLike: SetQuoteLike,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val send: Send,
    private val hasConnection: InternetConnection,
    private val intents: Intents,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel("", "", "")))
    val uiState: StateFlow<HomeState> = _uiState

    private val _likeState = MutableStateFlow(QuoteLikesState())
    val likesState: StateFlow<QuoteLikesState> = _likeState

    init {
        onCreate()
    }

    fun onCreate() {
        getQuote()
        hasConnectionFlow()
    }

    fun onActions(action: HomeActions) {
        when (action) {
            HomeActions.Info -> showInfoDialog()
            HomeActions.New -> loadNewRandomQuote()
            HomeActions.Send -> shareQuote()
            HomeActions.Buy -> goToBuyImage()
            HomeActions.Owner -> goToSearchOwner()
            HomeActions.Like -> setQuoteLike()
        }
    }

    private fun setQuoteLike() {
        viewModelScope.launch(dispatcherIO) {
            val isLike = !_likeState.value.isLike
            writeLog(LevelLog.INFO, "[HomeViewModel] -> setQuoteLike: $isLike")
            val updateLikes = LikesUiDTO(isLike = isLike)
            setLike(updateLikes.toDomain())
        }
    }

    private fun showInfoDialog() {
        _uiState.update { it.copy(showInfo = !it.showInfo) }
    }

    private fun loadNewRandomQuote() {
        if (!_uiState.value.showInterstitial) {
            _uiState.update { it.copy(showInterstitial = true) }
        } else {
            getRandomQuote()
            _uiState.update { it.copy(showInterstitial = false) }
        }
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

    private fun goToSearchOwner() {
        viewModelScope.launch {
            intents.goToSearchOwnerInBrowser(_uiState.value.quote.owner)
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

            getLikesQuote()
        }
    }

    private fun getLikesQuote() {
        viewModelScope.launch(dispatcherIO) {
            getLikes().collect { likes ->
                _likeState.update {
                    it.copy(
                        likes = likes?.likes ?: 0,
                        isLike = likes?.isLike ?: false
                    )
                }
            }
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

    private fun hasConnectionFlow() {
        viewModelScope.launch(dispatcherIO) {
            hasConnection.isConnectedFlow().collect { connection ->
                _uiState.update { it.copy(hasConnection = connection) }
            }
        }
    }

    /**
     * Function only for TESTING!!!
     *
     * ¡¡ Not call form UI !!
     */
    fun showInterstitialOnlyForTesting() {
        _uiState.update { it.copy(showInterstitial = true) }
    }
}

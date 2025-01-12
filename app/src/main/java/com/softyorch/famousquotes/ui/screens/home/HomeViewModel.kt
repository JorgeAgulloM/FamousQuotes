package com.softyorch.famousquotes.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.domain.useCases.GetQuoteStatistics
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.GetUserFavoriteQuote
import com.softyorch.famousquotes.domain.useCases.SetQuoteFavorite
import com.softyorch.famousquotes.domain.useCases.SetQuoteShown
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetUserLikeQuote
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.FavoritesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.model.QuoteFavoriteState
import com.softyorch.famousquotes.ui.screens.home.model.QuoteLikesState
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val selectQuote: GetTodayQuote,
    private val storage: IStorageService,
    private val setLike: SetQuoteLike,
    private val setShown: SetQuoteShown,
    private val setFavorite: SetQuoteFavorite,
    private val getUserLikeQuote: GetUserLikeQuote,
    private val getUserFavoriteQuote: GetUserFavoriteQuote,
    private val getQuoteStatistics: GetQuoteStatistics,
    private val shareQuote: ISend,
    private val intents: Intents,
    private val hasConnection: InternetConnection,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private var quoteJob: Job? = null
    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel.emptyModel()))
    val uiState: StateFlow<HomeState> = _uiState

    private var statisticsJob: Job? = null
    private val _uiStateStatistics: MutableStateFlow<QuoteStatistics> =
        MutableStateFlow(QuoteStatistics())
    val uiStateStatistics: StateFlow<QuoteStatistics> = _uiStateStatistics

    private var likeJob: Job? = null
    private val _likeState = MutableStateFlow(QuoteLikesState())
    val likesState: StateFlow<QuoteLikesState> = _likeState

    private var favoriteJob: Job? = null
    private val _favoriteState = MutableStateFlow(QuoteFavoriteState())
    val favoriteState: StateFlow<QuoteFavoriteState> = _favoriteState

    init {
        onCreate()
    }

    fun onCreate() {
        getQuote()
        hasConnectionFlow()
    }

    fun onActions(action: HomeActions) {
        Analytics.sendAction(Analytics.Action(action))
        writeLog(INFO, "[${HomeViewModel::class.java.simpleName}] onActions: $action")
        when (action) {
            is HomeActions.Info -> showInfoDialog()
            is HomeActions.NewQuoteRequest -> startProcessNewRandomQuote()
            is HomeActions.NewQuote -> loadNewRandomQuote()
            is HomeActions.ShareWithImage -> shareQuoteWithImage()
            is HomeActions.ShareText -> shareQuoteText()
            is HomeActions.Owner -> goToSearchOwner()
            is HomeActions.Like -> setQuoteLike()
            is HomeActions.Favorite -> setFavoriteQuote()
            is HomeActions.ShowImage -> showImage()
            is HomeActions.ShowNoConnectionDialog -> showConnectionDialog()
            is HomeActions.ReConnection -> getQuote()
            is HomeActions.ImageDownloadRequest -> startProcessDownloadImage()
            is HomeActions.DownloadImage -> downloadImage()
            is HomeActions.ShowToastDownload -> showDownloadedToast()
            is HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd -> closeOrErrorDownloadByBonifiedAd()
            is HomeActions.QuoteShown -> setQuoteShown()
        }
    }

    private fun shareQuoteText() {
        val dataToSend = "${_uiState.value.quote.body} '${_uiState.value.quote.owner}'"

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            shareQuote.shareTextTo(dataToSend)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun closeOrErrorDownloadByBonifiedAd() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun showInfoDialog() {
        _uiState.update { it.copy(showInfo = !it.showInfo) }
    }

    private fun startProcessNewRandomQuote() {
        if (!_uiState.value.showInterstitial)
            _uiState.update { it.copy(showInterstitial = true, isLoading = true) }
    }

    private fun loadNewRandomQuote() {
        _uiState.update { it.copy(showInterstitial = false, isLoading = false) }
        getRandomQuote()
    }

    private fun shareQuoteWithImage() {
        val dataToSend = "${_uiState.value.quote.body} '${_uiState.value.quote.owner}'"

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            shareQuote.shareImageTo(dataToSend, imageUri = _uiState.value.quote.imageUrl)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun goToSearchOwner() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(dispatcherDefault) {
                intents.goToSearchOwnerInBrowser(_uiState.value.quote.owner)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun setQuoteLike() {
        viewModelScope.launch(dispatcherDefault) {
            val isLike = !_likeState.value.isLike
            writeLog(INFO, "[HomeViewModel] -> setQuoteLike: $isLike")
            val id = _uiState.value.quote.id
            val updateLikes = LikesUiDTO(id = id, isLike = isLike)
            setLike(updateLikes.toDomain())
        }
    }

    private fun setFavoriteQuote() {
        viewModelScope.launch(dispatcherDefault) {
            val isFavorite = !_favoriteState.value.isFavorite
            writeLog(INFO, "[HomeViewModel] -> setQuoteFavorite: $isFavorite")
            val id = _uiState.value.quote.id
            val favoriteState = FavoritesUiDTO(id = id, isFavorite = isFavorite)
            setFavorite(favoriteState.toDomain())
        }
    }

    private fun showImage() {
        _uiState.update { it.copy(showImage = !_uiState.value.showImage) }
    }

    private fun showConnectionDialog() {
        val showSate = _uiState.value.showDialogNoConnection.let { !it }
        _uiState.update { it.copy(showDialogNoConnection = showSate) }
    }

    private fun getQuote() {
        quoteJob?.cancel()

        quoteJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            selectQuote().collect { quoteResult ->
                quoteResult?.let { quote ->
                    _uiState.update {
                        it.copy(
                            quote = quote.copy(
                                body = quote.body.trim(),
                                owner = if ("'" in quote.owner) {
                                    quote.owner.trim().replace("'", "")
                                } else {
                                    quote.owner.trim()
                                }
                            ),
                            showDialogNoConnection = it.hasConnection != true,
                        )
                    }
                    getStatistics(id = quote.id)
                }
            }
        }
    }

    private fun startProcessDownloadImage() {
        viewModelScope.launch(dispatcherDefault) {
            if (!_uiState.value.showInterstitial) {
                _uiState.update {
                    it.copy(
                        showInterstitial = true,
                        isLoading = true,
                        downloadImageRequest = true
                    )
                }
            }
        }
    }

    private fun downloadImage() {
        viewModelScope.launch(dispatcherDefault) {
            storage.downloadImage(_uiState.value.quote.id) { downloadResult ->
                _uiState.update {
                    it.copy(
                        downloadImage = downloadResult,
                        showInterstitial = false,
                        isLoading = false,
                        downloadImageRequest = false
                    )
                }
            }
        }
    }

    private fun showDownloadedToast() {
        _uiState.update { it.copy(downloadImage = false) }
    }

    private fun getStatistics(id: String) {
        statisticsJob?.cancel()

        getLikesQuote(id)
        getFavoriteQuote(id)
        statisticsJob = viewModelScope.launch(dispatcherDefault) {
            getQuoteStatistics(id).collect { statisticsResult ->
                statisticsResult?.let { statistics ->
                    _uiStateStatistics.update { statistics }
                }
            }
        }
    }

    private fun getLikesQuote(id: String) {
        likeJob?.cancel()

        likeJob = viewModelScope.launch(dispatcherDefault) {
            getUserLikeQuote(id).catch {
                writeLog(ERROR, "Error from getting user like: ${it.cause}", it)
            }.collect { isLike ->
                _likeState.update { it.copy(isLike = isLike ?: false) }
            }
        }
    }

    private fun getFavoriteQuote(id: String) {
        favoriteJob?.cancel()

        favoriteJob = viewModelScope.launch(dispatcherDefault) {
            getUserFavoriteQuote(id).catch {
                writeLog(ERROR, "Error from getting user favorite: ${it.cause}", it)
            }.collect { isFavorite ->
                _favoriteState.update { it.copy(isFavorite = isFavorite ?: false) }
            }
        }
    }

    private fun getRandomQuote() {
        viewModelScope.launch(dispatcherDefault) {
            _uiState.update { it.copy(isLoading = true) }

            selectQuote.getRandomQuote().collect { quoteResult ->
                quoteResult?.let { quote ->
                    _uiState.update {
                        it.copy(
                            quote = quote.copy(
                                body = quote.body.trim(),
                                owner = if ("'" in quote.owner) {
                                    quote.owner.trim().replace("'", "")
                                } else {
                                    quote.owner.trim()
                                }
                            ),
                            showDialogNoConnection = it.hasConnection != true
                        )
                    }
                    getStatistics(id = quote.id)
                }
            }
        }
    }

    private fun setQuoteShown() {
        viewModelScope.launch {
            withContext(dispatcherDefault) {
                writeLog(INFO, "[HomeViewModel] -> setQuoteShown: ${_uiState.value.quote.id}")
                setShown(_uiState.value.quote.id)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun hasConnectionFlow() {
        viewModelScope.launch(dispatcherDefault) {
            hasConnection.isConnectedFlow().catch {
                writeLog(ERROR, "Error getting connection state: ${it.cause}", it)
            }.onEach { connection ->
                if (isNeedActionWhenReconnection(connection))
                    onActions(HomeActions.ReConnection())
            }.collect { connection ->
                _uiState.update {
                    it.copy(
                        hasConnection = connection,
                        showDialogNoConnection = !connection
                    )
                }
            }
        }
    }

    private fun isNeedActionWhenReconnection(connection: Boolean) =
        _uiState.value.hasConnection != true && connection && !hasImageFromWeb() && !_uiState.value.isLoading

    private fun hasImageFromWeb() = _uiState.value.quote.imageUrl.startsWith(HTTP)

    /**
     * Function only for TESTING!!!
     *
     * ¡¡ Not call form UI !!
     */
    fun showInterstitialOnlyForTesting() {
        _uiState.update { it.copy(showInterstitial = true) }
    }

    companion object {
        const val HTTP = "http"
    }

}

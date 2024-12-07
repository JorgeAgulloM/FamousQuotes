package com.softyorch.famousquotes.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.SetQuoteShown
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikes
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetUserLikeQuote
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO
import com.softyorch.famousquotes.ui.screens.home.model.LikesUiDTO.Companion.toDomain
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.doesDownloadPathFileExist
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    private val getLikes: GetQuoteLikes,
    private val setLike: SetQuoteLike,
    private val storage: IStorageService,
    private val setQuoteShown: SetQuoteShown,
    private val getUserLikeQuote: GetUserLikeQuote,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
    private val shareQuote: ISend,
    private val hasConnection: InternetConnection,
    private val intents: Intents,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel.emptyModel()))
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
        Analytics.sendAction(Analytics.Action(action))
        writeLog(INFO, "[${HomeViewModel::class.java.simpleName}] onActions: $action")
        when (action) {
            is HomeActions.HideLoading -> hideLoading()
            is HomeActions.Info -> showInfoDialog()
            is HomeActions.New -> loadNewRandomQuote()
            is HomeActions.ShareWithImage -> shareQuoteWithImage()
            is HomeActions.ShareText -> shareQuoteText()
            is HomeActions.Owner -> goToSearchOwner()
            is HomeActions.Like -> setQuoteLike()
            is HomeActions.ShowImage -> showImage()
            is HomeActions.ShowNoConnectionDialog -> showConnectionDialog()
            is HomeActions.ReConnection -> getQuote()
            is HomeActions.DownloadImage -> downloadImage()
            is HomeActions.DownloadImageByBonifiedAd -> downloadImageByBonifiedAd()
            is HomeActions.ShowToastDownload -> showDownloadToast()
            is HomeActions.CloseDialogDownLoadImageAgain -> closeDownloadImageAgain()
            is HomeActions.SureDownloadImageAgain -> downloadImageAgain()
            is HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd -> closeOrErrorDownloadByBonifiedAd()
            is HomeActions.QuoteShown -> setQuoteShown()
        }
    }

    private fun hideLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun shareQuoteText() {
        val dataToSend = "${_uiState.value.quote.body} '${_uiState.value.quote.owner}'"

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            shareQuote.shareTextTo(dataToSend)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun downloadImageByBonifiedAd() {
        if (!_uiState.value.showBonified) {
            _uiState.update {
                it.copy(
                    showBonified = true,
                    isLoading = true
                )
            }
        }
    }

    private fun closeOrErrorDownloadByBonifiedAd() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun showInfoDialog() {
        _uiState.update { it.copy(showInfo = !it.showInfo) }
    }

    private fun loadNewRandomQuote() {
        if (!_uiState.value.showInterstitial) {
            _uiState.update { it.copy(showInterstitial = true, isLoading = true) }
        } else {
            getRandomQuote()
            _uiState.update { it.copy(showInterstitial = false) }
        }
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
            getLikesQuote(id)
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val quote = withContext(dispatcherDefault) {
                selectQuote()
            }
            val reviewQuote = quote.copy(
                body = quote.body.trim(),
                owner = if ("'" in quote.owner) {
                    quote.owner.trim().replace("'", "")
                } else {
                    quote.owner.trim()
                }
            )
            _uiState.update {
                it.copy(
                    //isLoading = false,
                    quote = reviewQuote,
                    showDialogNoConnection = it.hasConnection != true
                )
            }

            getLikesQuote(quote.id)
        }
    }

    private fun downloadImage() {
        viewModelScope.launch(dispatcherDefault) {
            val imageName = uiState.value.quote.id

            if (_uiState.value.showBonified) {
                storage.downloadImage(imageName) { downloadResult ->
                    _uiState.update {
                        it.copy(
                            showBonified = false,
                            downloadImage = downloadResult,
                            isLoading = false
                        )
                    }
                }
            } else {
                val imageIsExist = _uiState.value.imageIsDownloadAlready

                if (!imageIsExist && doesDownloadPathFileExist(imageName)) {
                    _uiState.update { it.copy(imageIsDownloadAlready = true) }
                } else {
                    storage.downloadImage(imageName) { downloadResult ->
                        _uiState.update { it.copy(downloadImage = downloadResult) }
                    }
                }
            }
        }
    }

    private fun showDownloadToast() {
        _uiState.update { it.copy(downloadImage = false) }
    }

    private fun closeDownloadImageAgain() {
        _uiState.update { it.copy(imageIsDownloadAlready = false) }
    }

    private fun downloadImageAgain() {
        viewModelScope.launch {
            if (!_uiState.value.showInterstitial) {
                _uiState.update {
                    it.copy(
                        showInterstitial = true,
                        isLoading = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        showInterstitial = false,
                        imageIsDownloadAlready = false,
                        isLoading = false
                    )
                }
                storage.downloadImage(_uiState.value.quote.id) { downloadResult ->
                    _uiState.update { it.copy(downloadImage = downloadResult) }
                }
            }
        }
    }

    private fun getLikesQuote(id: String) {
        viewModelScope.launch(dispatcherDefault) {
            getLikes(id).catch {
                writeLog(ERROR, "Error from getting likes: ${it.cause}", it)
            }.collect { likes ->
                _likeState.update { it.copy(likes = likes?.likes ?: 0) }
            }
        }
        viewModelScope.launch(dispatcherDefault) {
            getUserLikeQuote(id).catch {
                writeLog(ERROR, "Error from getting user like: ${it.cause}", it)
            }.collect { isLike ->
                _likeState.update { it.copy(isLike = isLike ?: false) }
            }
        }
    }

    private fun getRandomQuote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val quote = withContext(dispatcherDefault) {
                selectQuote.getRandomQuote()
            }
            getLikesQuote(quote.id)
            _uiState.update { it.copy(quote = quote) }
        }
    }

    private fun setQuoteShown() {
        viewModelScope.launch {
            withContext(dispatcherDefault) {
                setQuoteShown(_uiState.value.quote.id)
            }
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

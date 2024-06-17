package com.softyorch.famousquotes.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.core.Send
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.useCases.GetTodayQuote
import com.softyorch.famousquotes.domain.useCases.billing.BillingModel
import com.softyorch.famousquotes.domain.useCases.billing.BillingPurchase
import com.softyorch.famousquotes.domain.useCases.billing.BillingStart
import com.softyorch.famousquotes.domain.useCases.quoteLikes.GetQuoteLikes
import com.softyorch.famousquotes.domain.useCases.quoteLikes.SetQuoteLike
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
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
    private val billingStartUseCase: BillingStart,
    private val billingLaunchPurchase: BillingPurchase,
    private val storage: IStorageService,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val send: Send,
    private val hasConnection: InternetConnection,
    private val intents: Intents,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState(quote = FamousQuoteModel.emptyModel()))
    val uiState: StateFlow<HomeState> = _uiState

    private val _likeState = MutableStateFlow(QuoteLikesState())
    val likesState: StateFlow<QuoteLikesState> = _likeState

    private val _billingData = MutableStateFlow(BillingModel.empty())
    //val billingData: StateFlow<BillingModel> = _billingData

    init {
        onCreate()
    }

    fun onCreate() {
        getQuote()
        hasConnectionFlow()
    }

    fun onActions(action: HomeActions) {
        Analytics.sendAction(Analytics.Action(action))
        when (action) {
            is HomeActions.Info -> showInfoDialog()
            is HomeActions.New -> loadNewRandomQuote()
            is HomeActions.Send -> shareQuote()
            is HomeActions.Buy -> purchaseLaunch()
            is HomeActions.Owner -> goToSearchOwner()
            is HomeActions.Like -> setQuoteLike()
            is HomeActions.ShowImage -> showImage()
            is HomeActions.ShowNoConnectionDialog -> showConnectionDialog()
            is HomeActions.ReConnection -> getQuote()
            is HomeActions.DownloadImage -> downloadImage()
            is HomeActions.ShowBuyDialog -> showBuyDialog()
            is HomeActions.DownloadImageByBonifiedAd -> downloadImageByBonifiedAd()
            is HomeActions.ShowToastDownload -> showDownloadToast()
            is HomeActions.CloseDialogDownLoadImageAgain -> closeDownloadImageAgain()
            is HomeActions.SureDownloadImageAgain -> downloadImageAgain()
            is HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd -> closeOrErrorDownloadByBonifiedAd()
        }
    }

    private fun showBuyDialog() {
        _uiState.update { it.copy(showBuyDialog = !it.showBuyDialog) }
    }

    private fun downloadImageByBonifiedAd() {
        if (!_uiState.value.showBonified) {
            _uiState.update {
                it.copy(
                    showBuyDialog = false,
                    showBonified = true,
                    isLoading = true
                )
            }
        }
    }

    private fun closeOrErrorDownloadByBonifiedAd() {
        _uiState.update { it.copy(showBuyDialog = false, isLoading = false) }
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

    private fun shareQuote() {
        val dataToSend = "${_uiState.value.quote.body} - ${_uiState.value.quote.owner}"

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(dispatcherIO) {
                send.sendDataTo(dataToSend)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun purchaseLaunch() {
        viewModelScope.launch(dispatcherIO) {
            billingLaunchPurchase(
                _uiState.value.quote.id,
                MainActivity.instance
            ).let { purchase ->
                _uiState.update { state -> state.copy(purchasedOk = purchase) }
                billingStartUseCase()
            }
        }
    }

    private fun goToSearchOwner() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(dispatcherIO) {
                intents.goToSearchOwnerInBrowser(_uiState.value.quote.owner)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun setQuoteLike() {
        viewModelScope.launch(dispatcherIO) {
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
        _uiState.update { it.copy(showDialogNoConnection = true) }
    }

    private fun getQuote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val quote = withContext(dispatcherIO) {
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
                    isLoading = false,
                    quote = reviewQuote,
                    showDialogNoConnection = if (it.hasConnection != true) false else null
                )
            }

            getLikesQuote(quote.id)
            connectToBilling()
        }
    }

    private fun downloadImage() {
        viewModelScope.launch(dispatcherIO) {
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
        viewModelScope.launch(dispatcherIO) {
            getLikes(id).catch {
                writeLog(ERROR, "Error from getting likes: ${it.cause}")
            }.collect { likes ->
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
            getLikesQuote(quote.id)
            _uiState.update { it.copy(isLoading = false, quote = quote) }
        }
    }

    private fun hasConnectionFlow() {
        viewModelScope.launch(dispatcherIO) {
            hasConnection.isConnectedFlow().catch {
                writeLog(ERROR, "Error getting connection state: ${it.cause}")
            }.onEach { connection ->
                if (_uiState.value.hasConnection != true && connection)
                    onActions(HomeActions.ReConnection())
                if (_uiState.value.showImage)
                    onActions(HomeActions.ShowImage())
            }.collect { connection ->
                _uiState.update {
                    it.copy(
                        hasConnection = connection,
                        showDialogNoConnection = if (!connection) false else null
                    )
                }
            }
        }
    }

    private fun connectToBilling() {
        viewModelScope.launch {
            val resultModel = withContext(dispatcherIO) {
                billingStartUseCase()
            }
            _billingData.update { resultModel }

            resultModel.productsPurchased.forEach { productId ->
                if (productId == _uiState.value.quote.id) {
                    _uiState.update { it.copy(purchasedOk = Purchase.PurchaseState.PURCHASED) }
                    return@forEach
                }
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

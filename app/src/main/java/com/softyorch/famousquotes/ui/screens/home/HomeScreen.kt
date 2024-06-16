package com.softyorch.famousquotes.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Bonified
import com.softyorch.famousquotes.ui.admob.BonifiedAdState
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.admob.InterstitialAdState
import com.softyorch.famousquotes.ui.components.LoadingCircle
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedContentHome
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedImage
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.BasicDialogApp
import com.softyorch.famousquotes.ui.screens.home.components.BuyActions
import com.softyorch.famousquotes.ui.screens.home.components.BuyImageDialog
import com.softyorch.famousquotes.ui.screens.home.components.CardControls
import com.softyorch.famousquotes.ui.screens.home.components.InfoDialog
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.TextBody
import com.softyorch.famousquotes.ui.screens.home.components.TextInfoApp
import com.softyorch.famousquotes.ui.screens.home.components.TextOwner
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick
import com.softyorch.famousquotes.ui.screens.home.components.TopControls
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.theme.brushBackGround2
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val interstitial = Interstitial.instance
    val bonified = Bonified.instance

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val stateLikes: QuoteLikesState by viewModel.likesState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    interstitial.Show(state.showInterstitial) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(INFO, "itState: $it")
            if (!state.imageIsDownloadAlready)
                viewModel.onActions(HomeActions.New())
        }

        if (it is InterstitialAdState.Clicked ||
            it is InterstitialAdState.Close ||
            it is InterstitialAdState.Error
        ) {
            if (state.imageIsDownloadAlready)
                viewModel.onActions(HomeActions.SureDownloadImageAgain())
        }
    }

    bonified.Show(state.showBonified) { bonifiedState ->
        if (bonifiedState == BonifiedAdState.Reward) {
            viewModel.onActions(HomeActions.DownloadImage())
        }
        if (
            bonifiedState == BonifiedAdState.Showed ||
            bonifiedState == BonifiedAdState.Close ||
            bonifiedState == BonifiedAdState.Error ||
            bonifiedState == BonifiedAdState.OnDismissed
        ) {
            viewModel.onActions(HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(top = 2.dp).background(
            brushBackGround(), shape = MaterialTheme.shapes.extraLarge.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize
            )
        )
    ) {

        Box(modifier = Modifier.fillMaxWidth().zIndex(10f), contentAlignment = Alignment.TopEnd) {
            val toastMsg = stringResource(R.string.main_info_dialog_connection)
            val hasConnection = state.hasConnection == true
            val imageFromWeb = state.quote.imageUrl.startsWith("http")
            TopControls(
                hasText = state.quote.body,
                isPurchased = state.purchasedOk,
                isEnabled = hasConnection,
                isImageExt = imageFromWeb
            ) { action ->
                when (action) {
                    is HomeActions.Buy -> if (hasConnection) viewModel.onActions(action)
                    else context.showToast(toastMsg, Toast.LENGTH_LONG)

                    else -> viewModel.onActions(action)
                }
            }
        }
        Box(
            modifier = Modifier.clickable {
                viewModel.onActions(HomeActions.ShowImage())
            },
            contentAlignment = Alignment.TopCenter
        ) {
            BackgroundImage(uri = state.quote.imageUrl)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            CardQuote(
                state = state,
                stateLikes = stateLikes,
                context = context
            ) { action ->
                viewModel.onActions(action)
            }
        }

        if (state.showInfo)
            InfoDialog { viewModel.onActions(HomeActions.Info()) }

        if (state.showDialogNoConnection == false) NoConnectionDialog {
            viewModel.onActions(HomeActions.ShowNoConnectionDialog())
        }

        if (state.isLoading) LoadingCircle()

        if (state.downloadImage) {
            context.showToast(
                stringResource(R.string.image_download_toast_success),
                Toast.LENGTH_LONG
            )
            viewModel.onActions(HomeActions.ShowToastDownload())
        }

        if (state.imageIsDownloadAlready)
            BasicDialogApp(
                text = stringResource(R.string.dialog_image_download_again_text),
                textBtnPositive = stringResource(R.string.dialog_image_download_again_cancel),
                textBtnNegative = stringResource(R.string.dialog_image_download_again_download),
            ) { action ->
                val homeAction = when (action) {
                    POSITIVE -> HomeActions.SureDownloadImageAgain()
                    NEGATIVE -> HomeActions.CloseDialogDownLoadImageAgain()
                }
                viewModel.onActions(homeAction)
            }

        if (state.showBuyDialog) BuyImageDialog(
            title = stringResource(R.string.dialog_download_image_title),
            textBtnPositive = stringResource(R.string.dialog_download_image_btn_view_ad),
            textBtnNegative = stringResource(R.string.dialog_download_image_btn_buy),
        ) {
            when (it) {
                BuyActions.BuyImage -> viewModel.apply {
                    onActions(HomeActions.Buy())
                    onActions(HomeActions.ShowBuyDialog())
                }

                BuyActions.ViewAdd -> viewModel.onActions(HomeActions.DownloadImageByBonifiedAd())
                BuyActions.Exit -> viewModel.onActions(HomeActions.ShowBuyDialog())
            }
        }
    }
}

@Composable
fun BackgroundImage(uri: String) {
    val context = LocalContext.current

    val data = if (uri.startsWith("http")) uri
    else context.getResourceDrawableIdentifier(uri)

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .size(Size.ORIGINAL) // Set the target size to load the image at.
            .build(),
        contentScale = ContentScale.Fit
    )

    val state = painter.state
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        AnimatedImage(
            isVisible = state is AsyncImagePainter.State.Success,
            painter = painter
        )
    }
}

@Composable
fun CardQuote(
    state: HomeState,
    stateLikes: QuoteLikesState,
    context: Context,
    onAction: (HomeActions) -> Unit,
) {
    val toastMsg = stringResource(R.string.main_info_dialog_connection)

    Column(
        modifier = Modifier.background(brush = brushBackGround2()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val isActive = state.quote.body.isNotBlank() && !state.showImage
        val hasConnection = state.hasConnection == true
        val imageFromWeb = state.quote.id.length > 3 && !state.quote.id.startsWith('0')
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().animateContentSize { _, _ -> },
        ) {
            AnimatedContentHome(isActive = isActive) {
                Column {
                    AppIcon()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextInfoApp(
                            text = stringResource(R.string.main_text_get_inspired),
                            size = 22,
                            color = SecondaryColor
                        )
                    }
                    TextBody(text = state.quote.body)
                    Row(
                        modifier = Modifier.background(color = Color.Transparent)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CardControls(
                            hasText = state.quote.body,
                            stateLikes = stateLikes,
                            disabledReload = state.showInterstitial,
                            isEnabled = hasConnection,
                            isQuoteFromService = imageFromWeb
                        ) { action ->
                            when (action) {
                                is HomeActions.New -> if (hasConnection) onAction(action)
                                else context.showToast(toastMsg, Toast.LENGTH_LONG)

                                is HomeActions.Owner -> if (hasConnection) onAction(action)
                                else context.showToast(toastMsg, Toast.LENGTH_LONG)

                                is HomeActions.Send -> if (hasConnection) onAction(action)
                                else context.showToast(toastMsg, Toast.LENGTH_LONG)

                                else -> onAction(action)
                            }
                        }
                        TextOwner(text = state.quote.owner) {
                            if (hasConnection) onAction(HomeActions.Owner())
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                        }
                    }
                }
            }
            AnimatedContentHome(isActive = state.showImage) {
                TextToClick(text = stringResource(R.string.main_info_click_another_on_image))
                // For Mode demo => Box(modifier = Modifier.fillMaxWidth().height(108.dp))
            }
        }
        Banner.bannerInstance.Show()
    }
}

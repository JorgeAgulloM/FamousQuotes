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
import androidx.compose.ui.unit.Dp
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
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedContentHome
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedImage
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.BasicDialogApp
import com.softyorch.famousquotes.ui.screens.home.components.CardControlsGroup
import com.softyorch.famousquotes.ui.screens.home.components.InfoDialog
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.TextBody
import com.softyorch.famousquotes.ui.screens.home.components.TextInfoApp
import com.softyorch.famousquotes.ui.screens.home.components.TextOwner
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick
import com.softyorch.famousquotes.ui.screens.home.components.TopControlsGroup
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.theme.brushBackGround2
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.isFullScreenMode
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun HomeScreen(viewModel: HomeViewModel, onNavigateToUserScreen: () -> Unit) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val interstitial = Interstitial.instance
    val bonified = Bonified.instance

    AdmobAds(state, interstitial, bonified) { action ->
        viewModel.onActions(action)
    }

    val stateLikes: QuoteLikesState by viewModel.likesState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isFullScreen = isFullScreenMode(MainActivity.instance)
    val paddingTop = if (isFullScreen) 32.dp else 0.dp
    ContentBody(paddingTop, state, stateLikes, context, onNavigateToUserScreen) { action ->
        viewModel.onActions(action)
    }
}

@Composable
private fun AdmobAds(
    state: HomeState,
    interstitial: Interstitial,
    bonified: Bonified,
    onActions: (HomeActions) -> Unit
) {

    interstitial.Show(state.showInterstitial) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(INFO, "itState: $it")
            if (!state.imageIsDownloadAlready) onActions(HomeActions.New())
        }

        if ((it is InterstitialAdState.Clicked ||
                    it is InterstitialAdState.Close ||
                    it is InterstitialAdState.Error) &&
            state.imageIsDownloadAlready
        ) onActions(HomeActions.SureDownloadImageAgain())

    }

    bonified.Show(state.showBonified) { bonifiedState ->
        if (bonifiedState == BonifiedAdState.Reward) onActions(HomeActions.DownloadImage())

        if (
            bonifiedState == BonifiedAdState.Showed ||
            bonifiedState == BonifiedAdState.Close ||
            bonifiedState == BonifiedAdState.Error ||
            bonifiedState == BonifiedAdState.OnDismissed
        ) {
            writeLog(INFO, "[HomeScreen] -> bonifiedState: $bonifiedState")
            onActions(HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd())
        }
    }
}

@Composable
private fun ContentBody(
    paddingTop: Dp,
    state: HomeState,
    stateLikes: QuoteLikesState,
    context: Context,
    onNavigateToUserScreen: () -> Unit,
    onActions: (HomeActions) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 2.dp)
            .background(
                brushBackGround(), shape = MaterialTheme.shapes.extraLarge.copy(
                    bottomStart = ZeroCornerSize,
                    bottomEnd = ZeroCornerSize
                )
            )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth().padding(top = paddingTop).zIndex(10f),
            contentAlignment = Alignment.TopEnd
        ) {
            val hasConnection = state.hasConnection == true
            val imageFromWeb = state.quote.imageUrl.startsWith("http")
            TopControlsGroup(
                hasText = state.quote.body,
                isEnabled = hasConnection,
                isImageExt = imageFromWeb,
                onNavigateToUserScreen = onNavigateToUserScreen
            ) { action -> onActions(action) }
        }
        Box(
            modifier = Modifier.clickable { onActions(HomeActions.ShowImage()) },
            contentAlignment = Alignment.TopCenter
        ) {
            BackgroundImage(uri = state.quote.imageUrl)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            CardQuote(
                state = state,
                stateLikes = stateLikes,
                context = context
            ) { action -> onActions(action) }
        }

        if (state.showInfo) InfoDialog { onActions(HomeActions.Info()) }

        if (state.showDialogNoConnection == false) NoConnectionDialog {
            onActions(HomeActions.ShowNoConnectionDialog())
        }

        if (state.isLoading) LoadingCircle()

        if (state.downloadImage) {
            context.showToast(
                stringResource(R.string.image_download_toast_success),
                Toast.LENGTH_LONG
            )
            onActions(HomeActions.ShowToastDownload())
        }

        if (state.imageIsDownloadAlready)
            BasicDialogApp(
                text = stringResource(R.string.dialog_image_download_again_text),
                textBtnPositive = stringResource(R.string.dialog_image_download_again_cancel),
                textBtnNegative = stringResource(R.string.dialog_image_download_again_download),
            ) { action ->
                when (action) {
                    POSITIVE -> onActions(HomeActions.CloseDialogDownLoadImageAgain())
                    NEGATIVE -> onActions(HomeActions.SureDownloadImageAgain())
                    DISMISS -> Unit
                }
            }
    }
}

@Composable
private fun BackgroundImage(uri: String) {
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
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        AnimatedImage(
            isVisible = state is AsyncImagePainter.State.Success,
            painter = painter
        )
    }
}

@Composable
private fun CardQuote(
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
                    HeaderQuote()
                    TextBody(text = state.quote.body)
                    BottomBar(
                        state = state,
                        stateLikes = stateLikes,
                        hasConnection = hasConnection,
                        imageFromWeb = imageFromWeb,
                        context = context,
                        toastMsg = toastMsg,
                        onAction = onAction
                    )
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

@Composable
private fun HeaderQuote() {
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
}

@Composable
private fun BottomBar(
    state: HomeState,
    stateLikes: QuoteLikesState,
    hasConnection: Boolean,
    imageFromWeb: Boolean,
    context: Context,
    toastMsg: String,
    onAction: (HomeActions) -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardControlsGroup(
            hasText = state.quote.body,
            stateLikes = stateLikes,
            disabledReload = state.showInterstitial,
            isEnabled = hasConnection,
            isQuoteFromService = imageFromWeb
        ) { action ->
            when (action) {
                is HomeActions.New,
                is HomeActions.Owner,
                is HomeActions.ShareWithImage,
                is HomeActions.ShareText -> if (hasConnection)
                    onAction(action)
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

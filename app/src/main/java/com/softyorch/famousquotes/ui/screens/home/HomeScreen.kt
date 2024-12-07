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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CornerBasedShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.softyorch.famousquotes.ui.components.IsDebugShowText
import com.softyorch.famousquotes.ui.components.LoadingCircle
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel.Companion.HTTP
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedContentHome
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedImage
import com.softyorch.famousquotes.ui.screens.home.components.BasicDialogApp
import com.softyorch.famousquotes.ui.screens.home.components.CardControlsGroup
import com.softyorch.famousquotes.ui.screens.home.components.InfoDialog
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.screens.home.components.TextBody
import com.softyorch.famousquotes.ui.screens.home.components.TextOwner
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick
import com.softyorch.famousquotes.ui.screens.home.components.TopControlsGroup
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
    val paddingTop = with(LocalDensity.current) {
        androidx.compose.foundation.layout.WindowInsets.statusBars.getTop(this).toDp()
    }

    ContentBody(
        paddingTop = paddingTop,
        state = state,
        stateLikes = stateLikes,
        context = context,
        onNavigateToUserScreen = onNavigateToUserScreen
    ) { action ->
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
    if (state.hasConnection == true && state.showInterstitial)
        ShowInterstitial(
            interstitial = interstitial,
            imageIsDownloadAlready = state.imageIsDownloadAlready,
            onActions = onActions
        )
    if (state.hasConnection == true && state.showBonified)
        ShowBonified(
            bonified = bonified,
            onActions = onActions
        )
}

@Composable
private fun ShowInterstitial(
    interstitial: Interstitial,
    showInterstitial: Boolean = true,
    imageIsDownloadAlready: Boolean,
    onActions: (HomeActions) -> Unit
) {
    interstitial.Show(showInterstitial) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(INFO, "itState: $it")
            if (!imageIsDownloadAlready) onActions(HomeActions.New())
        }

        if ((it is InterstitialAdState.Clicked ||
                    it is InterstitialAdState.Close ||
                    it is InterstitialAdState.Error) &&
            imageIsDownloadAlready
        ) onActions(HomeActions.SureDownloadImageAgain())
    }
}

@Composable
private fun ShowBonified(
    bonified: Bonified,
    showBonified: Boolean = true,
    onActions: (HomeActions) -> Unit
) {
    bonified.Show(showBonified) { bonifiedState ->
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
            .background(
                brushBackGround(),
                shape = configurableCornerShape()
            )
    ) {
        TopControlsGroup(
            hasText = state.quote.body,
            isEnabled = state.hasConnection == true,
            isImageExt = state.quote.imageUrl.startsWith("http"),
            isShoImage = state.showImage,
            paddingTop = paddingTop,
            onNavigateToUserScreen = onNavigateToUserScreen
        ) { action -> onActions(action) }

        BackgroundImage(uri = state.quote.imageUrl, context = context) { action -> onActions(action) }

        CardQuote(
            state = state,
            stateLikes = stateLikes,
            context = context
        ) { action -> onActions(action) }

        if (state.showInfo) InfoDialog { onActions(HomeActions.Info()) }

        if (state.showDialogNoConnection) NoConnectionDialog {
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

        if (state.imageIsDownloadAlready) BasicDialogApp(
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
private fun BackgroundImage(uri: String, context: Context, onActions: (HomeActions) -> Unit) {

    val data = if (uri.startsWith(HTTP)) uri
    else context.getResourceDrawableIdentifier(uri)

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .size(Size.ORIGINAL) // Set the target size to load the image at.
            .build(),
        contentScale = ContentScale.Fit
    )

    val painterState = painter.state
    Box(
        modifier = Modifier.clickable { onActions(HomeActions.ShowImage()) },
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = configurableCornerShape(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            AnimatedImage(
                isVisible = painterState is AsyncImagePainter.State.Success,
                painter = painter
            )
        }
    }
    if (painterState is AsyncImagePainter.State.Success) {
        onActions(HomeActions.HideLoading())
        onActions(HomeActions.QuoteShown())
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize { _, _ -> },
            ) {
                AnimatedContentHome(isActive = isActive) {
                    Column {
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
            IsDebugShowText(state.quote.id)
            SpacerHeight(Banner.heightBanner)
        }
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

@Composable
private fun configurableCornerShape(isCard: Boolean = false): CornerBasedShape {
    val isFullScreen = isFullScreenMode(MainActivity.instance)
    val cornerMaterialXL = MaterialTheme.shapes.extraLarge.topStart
    return MaterialTheme.shapes.extraLarge.copy(
        topStart = if (isFullScreen) ZeroCornerSize else cornerMaterialXL,
        topEnd = if (isFullScreen) ZeroCornerSize else cornerMaterialXL,
        bottomStart = if (!isCard) ZeroCornerSize else cornerMaterialXL,
        bottomEnd = if (!isCard) ZeroCornerSize else cornerMaterialXL
    )
}

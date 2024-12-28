package com.softyorch.famousquotes.ui.screens.home

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clip
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
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.admob.InterstitialAdState
import com.softyorch.famousquotes.ui.core.commonComponents.IsDebugShowText
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.core.commonComponents.TextOwner
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel.Companion.HTTP
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedContentHome
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedImage
import com.softyorch.famousquotes.ui.screens.home.components.CardControlsGroup
import com.softyorch.famousquotes.ui.screens.home.components.InfoDialog
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.screens.home.components.TextBody
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick
import com.softyorch.famousquotes.ui.screens.home.components.TopControlsGroup
import com.softyorch.famousquotes.ui.screens.home.model.QuoteFavoriteState
import com.softyorch.famousquotes.ui.screens.home.model.QuoteLikesState
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.theme.brushBackGround2
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToSettings: () -> Unit
) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val interstitial = Interstitial.instance
    //val bonified = Bonified.instance

    AdmobAds(state, interstitial, /*bonified*/) { action ->
        viewModel.onActions(action)
    }

    val stateStatistics: QuoteStatistics by viewModel.uiStateStatistics.collectAsStateWithLifecycle()
    val stateLikes: QuoteLikesState by viewModel.likesState.collectAsStateWithLifecycle()
    val stateFavorite: QuoteFavoriteState by viewModel.favoriteState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val paddingTop = with(LocalDensity.current) {
        androidx.compose.foundation.layout.WindowInsets.statusBars.getTop(this).toDp()
    }

    ContentBody(
        paddingTop = paddingTop,
        state = state,
        stateStatistics = stateStatistics,
        stateLikes = stateLikes,
        stateFavorite = stateFavorite,
        context = context,
        onNavigateToUserScreen = onNavigateToUserScreen,
        onNavigateToSettings = onNavigateToSettings
    ) { action ->
        viewModel.onActions(action)
    }
}

@Composable
private fun AdmobAds(
    state: HomeState,
    interstitial: Interstitial,
    //bonified: Bonified,
    onActions: (HomeActions) -> Unit
) {
    if (state.hasConnection == true && state.showInterstitial)
        ShowInterstitial(
            interstitial = interstitial,
            onActions = onActions
        )
/*    if (state.hasConnection == true && state.showBonified)
        ShowBonified(
            bonified = bonified,
            onActions = onActions
        )*/
}

@Composable
private fun ShowInterstitial(
    interstitial: Interstitial,
    showInterstitial: Boolean = true,
    onActions: (HomeActions) -> Unit
) {
    interstitial.Show(showInterstitial) {
        writeLog(INFO, "[Interstitial] -> OnAction: $it")

        if ((it is InterstitialAdState.Showed || it is InterstitialAdState.Error))
            onActions(HomeActions.NewQuote())
    }
}

/*@Composable
private fun ShowBonified(
    bonified: Bonified,
    showBonified: Boolean = true,
    onActions: (HomeActions) -> Unit
) {
    bonified.Show(showBonified) { bonifiedState ->
        writeLog(INFO, "[HomeScreen] -> bonifiedState: $bonifiedState")

        if (bonifiedState == BonifiedAdState.Reward) onActions(HomeActions.DownloadImage())

        // Estudiar como advertir al usuairo que debe esperar la finalización del ad
        if (
            bonifiedState == BonifiedAdState.Showed ||
            bonifiedState == BonifiedAdState.Close ||
            bonifiedState == BonifiedAdState.Error ||
            bonifiedState == BonifiedAdState.OnDismissed
        ) {
            onActions(HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd())
        }
    }
}*/

@Composable
private fun ContentBody(
    paddingTop: Dp,
    state: HomeState,
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
    context: Context,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
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
            disabledReload = state.showInterstitial,
            paddingTop = paddingTop,
            onNavigateToUserScreen = onNavigateToUserScreen,
            onNavigateToSettings = onNavigateToSettings
        ) { action -> onActions(action) }

        BackgroundImage(
            uri = state.quote.imageUrl,
            context = context
        ) { action -> onActions(action) }

        CardQuote(
            state = state,
            stateStatistics = stateStatistics,
            stateLikes = stateLikes,
            stateFavorite = stateFavorite,
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

        BackHandler {
            (context as? Activity)?.finish()
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
    Box(contentAlignment = Alignment.TopCenter) {
        Card(
            modifier = Modifier
                .clip(configurableCornerShape())
                .clickable { onActions(HomeActions.ShowImage()) },
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
        writeLog(INFO, "[HomeScreen] -> Show image url: $data")
        onActions(HomeActions.QuoteShown())
    }
}

@Composable
private fun CardQuote(
    state: HomeState,
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
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
            IsDebugShowText(state.quote)

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
                        TextOwner(text = state.quote.owner) {
                            if (hasConnection) onAction(HomeActions.Owner())
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                        }
                        BottomBar(
                            state = state,
                            stateStatistics = stateStatistics,
                            stateLikes = stateLikes,
                            stateFavorite = stateFavorite,
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
                }
            }
            SpacerHeight(Banner.heightBanner)
        }
    }
}

@Composable
private fun BottomBar(
    state: HomeState,
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
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
            stateStatistics = stateStatistics,
            stateLikes = stateLikes,
            stateFavorite = stateFavorite,
            isEnabled = hasConnection,
            isQuoteFromService = imageFromWeb
        ) { action ->
            when (action) {
                is HomeActions.Owner,
                is HomeActions.ShareWithImage,
                is HomeActions.ShareText -> if (hasConnection)
                    onAction(action)
                else context.showToast(toastMsg, Toast.LENGTH_LONG)

                else -> onAction(action)
            }
        }
    }
}

@Composable
private fun configurableCornerShape(isCard: Boolean = true): CornerBasedShape {
    val cornerMaterialXL = MaterialTheme.shapes.extraLarge.topStart
    return MaterialTheme.shapes.extraLarge.copy(
        topStart = ZeroCornerSize,
        topEnd = ZeroCornerSize,
        bottomStart = if (isCard) cornerMaterialXL else ZeroCornerSize,
        bottomEnd = if (isCard) cornerMaterialXL else ZeroCornerSize
    )
}

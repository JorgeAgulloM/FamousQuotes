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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.admob.InterstitialAdState
import com.softyorch.famousquotes.ui.components.LoadingCircle
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedContentHome
import com.softyorch.famousquotes.ui.screens.home.components.AnimatedImage
import com.softyorch.famousquotes.ui.screens.home.components.Controls
import com.softyorch.famousquotes.ui.screens.home.components.InfoDialog
import com.softyorch.famousquotes.ui.screens.home.components.NoConnectionDialog
import com.softyorch.famousquotes.ui.screens.home.components.TextBody
import com.softyorch.famousquotes.ui.screens.home.components.TextInfo
import com.softyorch.famousquotes.ui.screens.home.components.TextOwner
import com.softyorch.famousquotes.ui.screens.home.components.TextToClick
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val stateLikes: QuoteLikesState by viewModel.likesState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Interstitial(state.showInterstitial) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(INFO, "itState: $it")
            if (!state.imageIsDownloadAlready)
                viewModel.onActions(HomeActions.New)
        }

        if (it is InterstitialAdState.Clicked ||
            it is InterstitialAdState.Close ||
            it is InterstitialAdState.Error
        ) {
            if (state.imageIsDownloadAlready)
                viewModel.onActions(HomeActions.SureDownloadImageAgain)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(brushBackGround())) {

        Box(
            modifier = Modifier.clickable {
                viewModel.onActions(HomeActions.ShowImage)
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
            InfoDialog { viewModel.onActions(HomeActions.Info) }

        if (state.showDialogNoConnection == false) NoConnectionDialog {
            viewModel.onActions(HomeActions.ShowNoConnectionDialog)
        }

        if (state.isLoading) LoadingCircle()

        if (state.downloadImage) {
            context.showToast("Imagen descargada")
            viewModel.onActions(HomeActions.ShowToastDownload)
        }

        if (state.imageIsDownloadAlready)
            BasicAlertDialog(
                onDismissRequest = { viewModel.onActions(HomeActions.CloseDialogDownLoadImageAgain) },
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.extraLarge
                )
            ) {
                Column(
                    modifier = Modifier.background(
                        brush = brushBackGround(),
                        shape = MaterialTheme.shapes.extraLarge
                    ).padding(8.dp)
                ) {
                    TextInfo("Ya has bajado la imagen.\n¿Estás seguro de querer descargarla de nuevo?")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { viewModel.onActions(HomeActions.SureDownloadImageAgain) }
                        ) {
                            Text(text = "SI")
                        }
                        Button(
                            onClick = { viewModel.onActions(HomeActions.CloseDialogDownLoadImageAgain) }
                        ) {
                            Text(text = "NO")
                        }
                    }
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
            .build()
    )

    val state = painter.state

    AnimatedImage(
        isVisible = state is AsyncImagePainter.State.Success,
        painter = painter
    )
}

@Composable
fun CardQuote(
    state: HomeState,
    stateLikes: QuoteLikesState,
    context: Context,
    onAction: (HomeActions) -> Unit,
) {
    val toastMsg = stringResource(R.string.main_info_dialog_connection)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.background(brush = brushBackGround()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val isActive = state.quote.body.isNotBlank() && !state.showImage
            val hasConnection = state.hasConnection == true
            val imageFromWeb = state.quote.imageUrl.startsWith("http")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().animateContentSize { _, _ -> },
            ) {
                Controls(
                    hasText = state.quote.body,
                    isPurchased = state.purchasedOk,
                    stateLikes = stateLikes,
                    disabledReload = state.showInterstitial,
                    isEnabled = hasConnection,
                    isImageExt = imageFromWeb
                ) { action ->
                    when (action) {
                        HomeActions.Buy -> if (hasConnection) onAction(action)
                        else context.showToast(toastMsg, Toast.LENGTH_LONG)

                        HomeActions.New -> if (hasConnection) onAction(action)
                        else context.showToast(toastMsg, Toast.LENGTH_LONG)

                        HomeActions.Owner -> if (hasConnection) onAction(action)
                        else context.showToast(toastMsg, Toast.LENGTH_LONG)

                        else -> onAction(action)
                    }
                }
                AnimatedContentHome(isActive = isActive) {
                    Column {
                        TextBody(text = state.quote.body)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextOwner(text = state.quote.owner) {
                                if (hasConnection) onAction(HomeActions.Owner)
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
}

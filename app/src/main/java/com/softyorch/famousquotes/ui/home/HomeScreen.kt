package com.softyorch.famousquotes.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.admob.Interstitial
import com.softyorch.famousquotes.ui.admob.InterstitialAdState
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.showToast
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.showInterstitial) Interstitial(true) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(LevelLog.INFO, "itState: $it")
            viewModel.onActions(HomeActions.New)
        }
    }

    Box(contentAlignment = Alignment.Center) {
        BackgroundImage(uri = state.quote.imageUrl)
        CardQuote(state = state) { action ->
            viewModel.onActions(action)
        }
        if (state.showInfo)
            InfoDialog { viewModel.onActions(HomeActions.Info) }
    }
}

@Composable
fun BackgroundImage(uri: String) {
    Box(modifier = Modifier.fillMaxSize()) {

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
        AnimatedImage(
            isVisible = state !is AsyncImagePainter.State.Success,
            painter = painterResource(R.drawable.loading_1)
        )
    }
}

@Composable
fun CardQuote(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
) {
    val context = LocalContext.current
    val toastMsg = stringResource(R.string.main_info_dialog_connection)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(2f),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(brush = brushBackGround())
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Controls(
                        disabledReload = state.showInterstitial,
                        hasConnection = state.hasConnection
                    ) { action ->
                        when (action) {
                            HomeActions.Buy -> if (state.hasConnection) onAction(action)
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                            HomeActions.New -> if (state.hasConnection) onAction(action)
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                            HomeActions.Owner -> if (state.hasConnection) onAction(action)
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                            HomeActions.Info -> onAction(action)
                            HomeActions.Send -> onAction(action)
                        }
                    }
                    TextBody(text = state.quote.body)
                    SpacerHeight(height = 24)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextOwner(text = state.quote.owner) {
                            if (state.hasConnection) onAction(HomeActions.Owner)
                            else context.showToast(toastMsg, Toast.LENGTH_LONG)
                        }
                    }
                }
                Banner()
            }
        }
    }
}

@Composable
fun Controls(disabledReload: Boolean, hasConnection: Boolean, onAction: (HomeActions) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(end = 16.dp),
        horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top
    ) {
        if (!hasConnection) IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_lost_connection),
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Outlined.WifiOff
        ) { onAction(HomeActions.New) }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_info),
            icon = Icons.Outlined.Info
        ) { onAction(HomeActions.Info) }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_buy_image),
            icon = Icons.Outlined.LocalMall
        ) { onAction(HomeActions.Buy) }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_other_quote),
            icon = Icons.Outlined.RestartAlt,
            isEnabled = !disabledReload
        ) { onAction(HomeActions.New) }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_share),
            icon = Icons.Outlined.Share
        ) {
            onAction(
                HomeActions.Send
            )
        }
    }
}

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = PrimaryColor,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = { onClick() }, colors = IconButtonDefaults.iconButtonColors(
            contentColor = color
        ),
        enabled = isEnabled
    ) {
        Icon(imageVector = icon, contentDescription = cDescription)
    }
}

@Composable
fun TextInfo(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MyTypography.labelLarge
        )
    }
}

@Composable
fun TextBody(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = "\"$text\"",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MyTypography.displayLarge
        )
    }
}

@Composable
fun TextOwner(text: String, onClick: () -> Unit) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp)
                .clip(shape = MaterialTheme.shapes.large)
                .clickable { onClick() },
            style = MyTypography.labelLarge,
            textDecoration = TextDecoration.Underline,
            color = PrimaryColor
        )
    }
}

@Composable
fun AnimatedTextHome(text: String, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = text.isNotBlank(),
        enter = fadeIn(
            animationSpec = spring(0.8f, 0.8f),
            initialAlpha = 0f
        )
    ) { content() }
}

@Composable
fun InfoDialog(onAction: () -> Unit) {
    Dialog(onDismissRequest = { onAction() }) {
        Column(
            modifier = Modifier.background(
                brush = brushBackGround(),
                shape = MaterialTheme.shapes.extraLarge
            ).padding(16.dp)
        ) {
            InfoIcons(
                icon = Icons.Outlined.Info,
                text = stringResource(R.string.main_info_dialog_text_info)
            )
            SpacerHeight(height = 32)
            InfoIcons(
                icon = Icons.Outlined.LocalMall,
                text = stringResource(R.string.main_info_dialog_text_buy_image)
            )
            SpacerHeight()
            InfoIcons(
                icon = Icons.Outlined.RestartAlt,
                text = stringResource(R.string.main_info_dialog_text_other_quote)
            )
            SpacerHeight()
            InfoIcons(
                icon = Icons.Outlined.Share,
                text = stringResource(R.string.main_info_dialog_text_)
            )
            SpacerHeight()
            InfoIcons(
                icon = Icons.Outlined.Person,
                text = stringResource(R.string.main_info_dialog_owner)
            )
            SpacerHeight()
            InfoIcons(
                icon = Icons.Outlined.WifiOff,
                tint = MaterialTheme.colorScheme.error,
                text = stringResource(R.string.main_info_dialog_connection)
            )
        }
    }
}

@Composable
fun InfoIcons(icon: ImageVector, tint: Color = PrimaryColor, text: String) {
    Row {
        Icon(imageVector = icon, contentDescription = text, tint = tint)
        TextInfo(text)
    }
}

@Composable
fun SpacerHeight(height: Int = 16) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun AnimatedImage(isVisible: Boolean, painter: Painter) {
    val modifier = Modifier.fillMaxWidth().height(300.dp)
    val scale = ContentScale.Crop

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = spring(1f, 12f)
        ) + fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = shrinkVertically(
            animationSpec = spring(1f, 12f)
        ) + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.main_content_desc_image),
            modifier = modifier,
            contentScale = scale
        )
    }
}

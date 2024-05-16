package com.softyorch.famousquotes.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.softyorch.famousquotes.ui.theme.BackgroundColor
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
    val stateLikes: QuoteLikesState by viewModel.likesState.collectAsStateWithLifecycle()
    val showImageMsg = "Pulsa de nuevo en la imagen ver la frase!"
    val context = LocalContext.current

    Interstitial(state.showInterstitial) {
        if (it is InterstitialAdState.Showed ||
            it is InterstitialAdState.Error
        ) {
            writeLog(LevelLog.INFO, "itState: $it")
            viewModel.onActions(HomeActions.New)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) LoadingCircle()
        Box(
            modifier = Modifier.clickable {
                viewModel.onActions(HomeActions.ShowImage).also {
                    if (!state.showImage) context.showToast(showImageMsg, Toast.LENGTH_LONG)
                }
            },
            contentAlignment = Alignment.TopCenter
        ) {
            BackgroundImage(uri = state.quote.imageUrl)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            CardQuote(state = state, stateLikes = stateLikes, context = context) { action ->
                viewModel.onActions(action)
            }
        }
        if (state.showInfo)
            InfoDialog { viewModel.onActions(HomeActions.Info) }
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
            AnimatedContentHome(isActive = isActive) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Controls(
                        hasText = state.quote.body,
                        stateLikes = stateLikes,
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
                            HomeActions.Like -> onAction(action)
                            HomeActions.ShowImage -> onAction(action)
                        }
                    }
                    SpacerHeight(height = 24)
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
            }
            SpacerHeight(height = 24)
            Banner()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Controls(
    hasText: String,
    stateLikes: QuoteLikesState,
    disabledReload: Boolean,
    hasConnection: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    AnimatedTextHome(hasText) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconLike =
                    if (stateLikes.isLike) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder
                val colorIconLike = if (stateLikes.isLike) Color.Red else Color.DarkGray

                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = PrimaryColor,
                            modifier = Modifier.offset((-16).dp, (16).dp)
                        ) {
                            Text(
                                text = stateLikes.likes.toString(),
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                ) {
                    IconButtonMenu(
                        cDescription = stringResource(R.string.main_icon_content_desc_info),
                        color = colorIconLike,
                        icon = iconLike
                    ) { onAction(HomeActions.Like) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
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
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(contentColor = color),
        modifier = Modifier.padding(end = 4.dp),
        enabled = isEnabled
    ) {
        Icon(imageVector = icon, contentDescription = cDescription, modifier = Modifier.size(32.dp))
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
            text = text,
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
            textDecoration = TextDecoration.Underline
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
    val modifier = Modifier.fillMaxWidth()
    val scale = ContentScale.Crop

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 2000)),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.main_content_desc_image),
            modifier = modifier,
            contentScale = scale
        )
    }
}

@Composable
fun AnimatedContentHome(isActive: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isActive,
        enter = slideInVertically(
            animationSpec = spring(1f, 20f),
            initialOffsetY = { it / 2 }
        ) + fadeIn(animationSpec = tween(durationMillis = 1000))
    ) { content() }
}

@Composable
fun LoadingCircle() {
    val value by rememberInfiniteTransition(label = "LoadingCircle").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ), label = "LoadingCircleInfinite"
    )

    val gradientBrush by remember {
        mutableStateOf(
            Brush.horizontalGradient(
                colors = listOf(PrimaryColor, BackgroundColor, PrimaryColor),
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .drawBehind {
                rotate(value) { drawCircle(gradientBrush, style = Stroke(width = 24.dp.value)) }
            }
            .size(128.dp)
        )
    }
}

package com.softyorch.famousquotes.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.RestartAlt
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.brushBackGround
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceStringComposable

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(contentAlignment = Alignment.Center) {
        BackgroundImage(uri = state.quote.imageUrl)
        CardQuote(body = state.quote.body, owner = state.quote.owner) { action ->
            viewModel.onActions(action)
        }
    }
}

@Composable
fun BackgroundImage(uri: String) {
    Box(modifier = Modifier.fillMaxSize()) {

        val context = LocalContext.current
        val modifier = Modifier.fillMaxWidth().height(300.dp)
        val scale = ContentScale.Crop

        val data = if (uri.startsWith("http")) uri else context.getResourceStringComposable(uri)

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(data)
                .crossfade(true)
                .size(Size.ORIGINAL) // Set the target size to load the image at.
                .build()
        )

        val state = painter.state

        AnimatedVisibility(
            visible = state is AsyncImagePainter.State.Success,
            enter = slideInVertically(
                animationSpec = spring(1f, 12f)
            ) + fadeIn(animationSpec = tween(durationMillis = 1000))
        ) {
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = modifier,
                contentScale = scale
            )
        }
        AnimatedVisibility(
            visible = state !is AsyncImagePainter.State.Success,
            exit = shrinkVertically(
                animationSpec = spring(1f, 12f)
            ) + fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            Image(
                painter = painterResource(R.drawable.loading_1),
                contentDescription = "Image",
                modifier = modifier,
                contentScale = scale
            )
        }
    }
}

@Composable
fun CardQuote(body: String, owner: String, onAction: (HomeActions) -> Unit) {
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
                    Controls{ onAction(it) }
                    TextHome(text = body, true)
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextHome(text = owner)
                    }
                }
                Banner()
            }
        }
    }
}

@Composable
fun Controls(onAction: (HomeActions) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(end = 16.dp),
        horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top
    ) {
        IconButtonMenu(text = "Info", icon = Icons.Outlined.Info) { onAction(HomeActions.Info) }
        IconButtonMenu(text = "Buy image", icon = Icons.Outlined.LocalMall) { onAction(HomeActions.Buy) }
        IconButtonMenu(text = "Otra frase", icon = Icons.Outlined.RestartAlt) { onAction(HomeActions.New) }
        IconButtonMenu(text = "Compartir", icon = Icons.AutoMirrored.Outlined.Send) { onAction(HomeActions.Send) }
    }
}

@Composable
fun IconButtonMenu(text: String, icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }, colors = IconButtonDefaults.iconButtonColors(
            contentColor = PrimaryColor
        )
    ) {
        Icon(imageVector = icon, contentDescription = text)
    }
}

@Composable
fun TextHome(text: String, isBody: Boolean = false) {
    val style = if (isBody) MyTypography.displayLarge else MyTypography.labelLarge
    AnimatedVisibility(
        visible = text.isNotBlank(),
        enter = fadeIn(
            animationSpec = spring(0.8f, 0.8f),
            initialAlpha = 0f
        )
    ) {
        Text(
            text = "\"$text\"",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = style
        )
    }
}

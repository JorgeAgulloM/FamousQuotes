package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.softyorch.famousquotes.R

@Composable
fun AnimatedContentHome(isActive: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isActive,
        enter = slideInVertically(
            animationSpec = spring(1f, 20f),
            initialOffsetY = { it / 2 }
        ) + fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = shrinkVertically(
            animationSpec = spring(1f, 20f),
            targetHeight = { -it / 2 }
        ) + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) { content() }
}

@Composable
fun AnimatedImage(isVisible: Boolean, painter: Painter) {
    val modifier = Modifier.fillMaxWidth()
    val scale = ContentScale.Crop

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
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
fun AnimatedTextHome(text: String, isVisible: Boolean = true, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = text.isNotBlank() && isVisible,
        enter = fadeIn(
            animationSpec = spring(0.8f, 20f),
            initialAlpha = 0f
        )
    ) { content() }
}

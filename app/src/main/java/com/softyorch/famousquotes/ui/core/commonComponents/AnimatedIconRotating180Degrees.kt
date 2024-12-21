package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun AnimatedIconRotating180Degrees(
    expanded: Boolean,
    iconFirst: ImageVector,
    iconSecond: ImageVector,
    shadowOn: Boolean,
    animatedType: AnimatedType,
    onCloseMenu: () -> Unit
) {

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "animateFloatAsState"
    )

    val modifier = when (animatedType) {
        AnimatedType.Rotate -> Modifier.graphicsLayer { rotationZ = rotation }
        AnimatedType.Flip -> Modifier.graphicsLayer { rotationX = rotation }
    }

    val icon = if (expanded) iconSecond else iconFirst
    writeLog(text = "AnimatedIconRotating180Degrees -> Expanded: $expanded")
    Box(modifier = modifier) {
        IconButtonMenu(
            cDescription = "Close menu",
            icon = icon,
            shadowOn = shadowOn,
            isEnabled = true
        ) {
            onCloseMenu()
        }
    }
}

sealed interface AnimatedType {
    data object Rotate : AnimatedType
    data object Flip : AnimatedType
}

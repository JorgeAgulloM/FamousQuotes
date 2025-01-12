package com.softyorch.famousquotes.ui.core.commonComponents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.utils.appIcon

@OptIn(ExperimentalMaterial3Api::class)
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
                colors = listOf(AppColorSchema.primary, AppColorSchema.background, AppColorSchema.primary),
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }

    val icon = appIcon()

    BasicAlertDialog(onDismissRequest = {}) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier
                .drawBehind {
                    rotate(value) { drawCircle(gradientBrush, style = Stroke(width = 24.dp.value)) }
                }
                .size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = icon, contentDescription = null, modifier = Modifier.size(80.dp))
            }
        }
    }
}

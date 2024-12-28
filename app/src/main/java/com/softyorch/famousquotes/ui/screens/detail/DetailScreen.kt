package com.softyorch.famousquotes.ui.screens.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.screens.detail.components.CardDetail
import com.softyorch.famousquotes.ui.screens.detail.components.SetDialogs
import com.softyorch.famousquotes.ui.screens.detail.model.DetailState
import com.softyorch.famousquotes.ui.screens.detail.model.QuoteDetailsModel
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    id: String,
    leftHanded: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: DetailViewModel,
    onBackNavigation: () -> Unit
) {

    val quote: QuoteDetailsModel by viewModel.quoteModel.collectAsStateWithLifecycle()
    val state: DetailState by viewModel.detailState.collectAsStateWithLifecycle()
    val statistics: QuoteStatistics by viewModel.statisticsModel.collectAsStateWithLifecycle()

    val finishAnimation =
        animatedVisibilityScope.transition.currentState == EnterExitState.Visible && !state.hideControls

    Scaffold(modifier = modifier.fillMaxSize(), containerColor = AppColorSchema.background) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                CardDetail(
                    id = id,
                    quote = quote,
                    state = state,
                    statistics = statistics,
                    leftHanded = leftHanded,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    finishAnimation = finishAnimation
                ) { action -> viewModel.setDetailAction(action, id) }
                if (finishAnimation) TopMenu(modifier, leftHanded, onBackNavigation)
            }
            Box(Modifier.height(Banner.heightBanner.dp)) {}
        }
        SetDialogs(state) {
            viewModel.setDetailAction(it, id)
        }
    }
}

@Composable
private fun TopMenu(
    modifier: Modifier,
    leftHanded: Boolean,
    onBackNavigation: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (leftHanded) Arrangement.Start else Arrangement.End
    ) {
        IconButtonMenu(
            cDescription = "Back",
            color = AppColorSchema.secondary.copy(alpha = 0.6f),
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            shadowOn = true
        ) { onBackNavigation() }
    }
}

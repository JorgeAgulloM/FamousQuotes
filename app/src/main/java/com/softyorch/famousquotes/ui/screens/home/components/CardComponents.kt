package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.ui.core.commonComponents.BasicDialogApp
import com.softyorch.famousquotes.ui.core.commonComponents.IconCard
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.screens.home.model.QuoteFavoriteState
import com.softyorch.famousquotes.ui.screens.home.model.QuoteLikesState
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE

@Composable
fun CardControlsGroup(
    hasText: String,
    leftHanded: Boolean,
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
    isEnabled: Boolean,
    isQuoteFromService: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    var showSendDialog by remember { mutableStateOf(false) }

    AnimatedTextHome(hasText) {
        if (leftHanded) CardControlsLeft(
            stateStatistics = stateStatistics,
            stateLikes = stateLikes,
            stateFavorite = stateFavorite,
            isQuoteFromService = isQuoteFromService,
            isEnabled = isEnabled,
            onAction = onAction
        ) {
            showSendDialog = true
        } else CardControlsNonLeft(
            stateStatistics = stateStatistics,
            stateLikes = stateLikes,
            stateFavorite = stateFavorite,
            isQuoteFromService = isQuoteFromService,
            isEnabled = isEnabled,
            onAction = onAction
        ) {
            showSendDialog = true
        }
    }

    if (showSendDialog) BasicDialogApp(
        text = stringResource(R.string.dialog_how_do_you_share),
        title = stringResource(R.string.dialog_share_title),
        textBtnNegative = stringResource(R.string.dialog_share_by_text),
        textBtnPositive = stringResource(R.string.dialog_share_by_image),
        blockDismissActions = true
    ) {
        when (it) {
            NEGATIVE -> onAction(HomeActions.ShareText())
            POSITIVE -> onAction(HomeActions.ShareWithImage())
            DISMISS -> Unit
        }
        showSendDialog = false
    }
}

@Composable
private fun CardControlsLeft(
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
    isQuoteFromService: Boolean,
    isEnabled: Boolean,
    onAction: (HomeActions) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row {
            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_like_use),
                icon = Icons.Rounded.FavoriteBorder,
                secondIcon = Icons.Rounded.Favorite,
                color = AppColorSchema.likeColor,
                colorIcon = AppColorSchema.whiteSmoke,
                isSelected = stateLikes.isLike,
                valueStatistic = stateStatistics.likes,
                isVisible = isQuoteFromService,
                isEnabled = isEnabled
            ) { onAction(HomeActions.Like()) }

            SpacerWidth(width = 8)

            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Default.StarOutline,
                secondIcon = Icons.Default.Star,
                color = AppColorSchema.favoriteColor,
                colorIcon = AppColorSchema.whiteSmoke,
                isSelected = stateFavorite.isFavorite,
                valueStatistic = stateStatistics.favorites,
                isVisible = isQuoteFromService,
                isEnabled = isEnabled
            ) { onAction(HomeActions.Favorite()) }

            SpacerWidth(width = 8)

            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Outlined.Share,
                isEnabled = isEnabled
            ) { onClick() }
        }

        Row {
            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Outlined.RemoveRedEye,
                valueStatistic = stateStatistics.showns,
                isEnabled = isEnabled
            ) { }
            SpacerWidth(8)
        }
    }
}


@Composable
private fun CardControlsNonLeft(
    stateStatistics: QuoteStatistics,
    stateLikes: QuoteLikesState,
    stateFavorite: QuoteFavoriteState,
    isQuoteFromService: Boolean,
    isEnabled: Boolean,
    onAction: (HomeActions) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Outlined.RemoveRedEye,
                valueStatistic = stateStatistics.showns,
                isEnabled = isEnabled
            ) { }
        }

        Row {
            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Outlined.Share,
                isEnabled = isEnabled
            ) { onClick() }

            SpacerWidth(width = 8)

            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_share),
                icon = Icons.Default.StarOutline,
                secondIcon = Icons.Default.Star,
                color = AppColorSchema.favoriteColor,
                colorIcon = AppColorSchema.whiteSmoke,
                isSelected = stateFavorite.isFavorite,
                valueStatistic = stateStatistics.favorites,
                isVisible = isQuoteFromService,
                isEnabled = isEnabled
            ) { onAction(HomeActions.Favorite()) }

            SpacerWidth(width = 8)

            IconCard(
                cDescription = stringResource(R.string.main_icon_content_desc_like_use),
                icon = Icons.Rounded.FavoriteBorder,
                secondIcon = Icons.Rounded.Favorite,
                color = AppColorSchema.likeColor,
                colorIcon = AppColorSchema.whiteSmoke,
                isSelected = stateLikes.isLike,
                valueStatistic = stateStatistics.likes,
                isVisible = isQuoteFromService,
                isEnabled = isEnabled
            ) { onAction(HomeActions.Like()) }

            SpacerWidth(8)
        }
    }
}

package com.softyorch.famousquotes.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.screens.home.QuoteLikesState
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE

@Composable
fun CardControlsGroup(
    hasText: String,
    stateLikes: QuoteLikesState,
    disabledReload: Boolean,
    isEnabled: Boolean,
    isQuoteFromService: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    var showSendDialog by remember { mutableStateOf(false) }

    AnimatedTextHome(hasText) {
        CardControls(
            stateLikes,
            isQuoteFromService,
            isEnabled,
            onAction,
            disabledReload
        ) {
            showSendDialog = true
        }
    }

    if (showSendDialog) BasicDialogApp(
        text = stringResource(R.string.dialog_how_do_you_share),
        title = stringResource(R.string.dialog_share_title),
        textBtnPositive = stringResource(R.string.dialog_share_by_image),
        textBtnNegative = stringResource(R.string.dialog_share_by_text),
        blackDismissActions = true
    ) {
        when (it) {
            POSITIVE -> onAction(HomeActions.ShareWithImage())
            NEGATIVE -> onAction(HomeActions.ShareText())
            DISMISS -> Unit
        }
        showSendDialog = false
    }
}

@Composable
private fun CardControls(
    stateLikes: QuoteLikesState,
    isQuoteFromService: Boolean,
    isEnabled: Boolean,
    onAction: (HomeActions) -> Unit,
    disabledReload: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconLike =
            if (stateLikes.isLike) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder
        val colorIconLike = if (stateLikes.isLike) Color.Red else SecondaryColor

        if (isQuoteFromService) BadgedBox(
            badge = {
                Badge(
                    containerColor = SecondaryColor.copy(alpha = 0.8f),
                    modifier = Modifier.offset((-8).dp, (-8).dp)
                ) {
                    Text(
                        text = stateLikes.likes.toString(),
                        fontSize = 14.sp,
                        color = WhiteSmoke
                    )
                }
            }
        ) {
            IconButtonMenu(
                cDescription = stringResource(R.string.main_icon_content_desc_like_use),
                color = colorIconLike,
                icon = iconLike,
                isEnabled = isEnabled
            ) { onAction(HomeActions.Like()) }
        }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_share),
            icon = Icons.Outlined.Share,
            isEnabled = isEnabled
        ) { onClick() }
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_other_quote),
            icon = Icons.Outlined.RestartAlt,
            isEnabled = !disabledReload && isEnabled
        ) { onAction(HomeActions.New()) }
    }
}

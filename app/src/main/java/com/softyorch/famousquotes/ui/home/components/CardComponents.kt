package com.softyorch.famousquotes.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.home.HomeActions
import com.softyorch.famousquotes.ui.home.QuoteLikesState
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke

@Composable
fun Controls(
    hasText: String,
    stateLikes: QuoteLikesState,
    disabledReload: Boolean,
    hasConnection: Boolean,
    isImageExt: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    AnimatedTextHome(hasText) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
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
                            containerColor = if (hasConnection) SecondaryColor else WhiteSmoke,
                            modifier = Modifier.offset((-16).dp, (16).dp)
                        ) {
                            Text(
                                text = if (hasConnection) stateLikes.likes.toString() else "0",
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                ) {
                    IconButtonMenu(
                        cDescription = stringResource(R.string.main_icon_content_desc_like_use),
                        color = if (hasConnection) colorIconLike else WhiteSmoke,
                        icon = iconLike,
                        isEnabled = hasConnection
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
                    icon = Icons.Outlined.Info,
                    isEnabled = hasConnection
                ) { onAction(HomeActions.Info) }
                IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_buy_image),
                    icon = Icons.Outlined.LocalMall,
                    isEnabled = isImageExt && hasConnection
                ) { onAction(HomeActions.Buy) }
                IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_other_quote),
                    icon = Icons.Outlined.RestartAlt,
                    isEnabled = !disabledReload && hasConnection
                ) { onAction(HomeActions.New) }
                IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_share),
                    icon = Icons.Outlined.Share,
                    isEnabled = hasConnection
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
fun TextOwner(text: String, onClick: () -> Unit) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                .clip(shape = MaterialTheme.shapes.large)
                .clickable { onClick() },
            style = MyTypography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            textDecoration = TextDecoration.Underline,
        )
    }
}

@Composable
fun TextBody(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            style = MyTypography.displayLarge
        )
    }
}

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = SecondaryColor,
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


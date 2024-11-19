package com.softyorch.famousquotes.ui.screens.home.components

import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.screens.home.QuoteLikesState
import com.softyorch.famousquotes.ui.theme.MyTypography
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.theme.WhiteSmoke
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.DISMISS
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.NEGATIVE
import com.softyorch.famousquotes.ui.utils.DialogCloseAction.POSITIVE
import com.softyorch.famousquotes.utils.sdk29AndDown
import com.softyorch.famousquotes.utils.showToast

@Composable
fun CardControls(
    hasText: String,
    stateLikes: QuoteLikesState,
    disabledReload: Boolean,
    isEnabled: Boolean,
    isQuoteFromService: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    var showSendDialog by remember { mutableStateOf(false) }

    AnimatedTextHome(hasText) {
        Row(
            modifier = Modifier.wrapContentHeight()
                .padding(start = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconLike =
                    if (stateLikes.isLike) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder
                val colorIconLike = if (stateLikes.isLike) Color.Red else WhiteSmoke

                if (isQuoteFromService) BadgedBox(
                    badge = {
                        Badge(
                            containerColor = SecondaryColor.copy(alpha = 0.8f),
                            modifier = Modifier.offset((-16).dp, (4).dp)
                        ) {
                            Text(
                                text = stateLikes.likes.toString(),
                                fontSize = 14.sp,
                                color = Color.DarkGray
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
                ) { showSendDialog = true }
                IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_other_quote),
                    icon = Icons.Outlined.RestartAlt,
                    isEnabled = !disabledReload && isEnabled
                ) { onAction(HomeActions.New()) }
            }
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
            DISMISS -> {}
        }
        showSendDialog = false
    }
}


@Composable
fun TopControls(
    hasText: String,
    isEnabled: Boolean,
    isImageExt: Boolean,
    onAction: (HomeActions) -> Unit,
) {
    var showPermissionRationaleDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val permission = permission.WRITE_EXTERNAL_STORAGE
    val permissionState = ContextCompat.checkSelfPermission(context, permission)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onAction(HomeActions.DownloadImage())
        } else {
            showPermissionRationaleDialog = true
        }
    }

    if (showPermissionRationaleDialog) BasicDialogApp(
        text = stringResource(R.string.dialog_permission_rationale_text),
        auxText = stringResource(R.string.dialog_permission_rationale_aux_text),
        textBtnPositive = stringResource(R.string.dialog_permission_rationale_ok),
        textBtnNegative = stringResource(R.string.dialog_permission_rationale_denied),
    ) { action ->
        when (action) {
            POSITIVE -> launcher.launch(permission)
            NEGATIVE -> context.showToast(context.getString(R.string.dialog_permission_rationale_denied_toast))
            DISMISS -> {}
        }
        showPermissionRationaleDialog = false
    }

    AnimatedTextHome(hasText) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isEnabled) IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_lost_connection),
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Outlined.WifiOff
                ) { onAction(HomeActions.New()) }
                IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_info),
                    icon = Icons.Outlined.Info,
                    isEnabled = isEnabled,
                    shadowOn = true
                ) { onAction(HomeActions.Info()) }
                if (isImageExt) IconButtonMenu(
                    cDescription = stringResource(R.string.main_icon_content_desc_buy_image),
                    icon = Icons.Outlined.Download,
                    shadowOn = true,
                    isEnabled = isEnabled
                ) {
                    if (sdk29AndDown && permissionState != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                permission
                            )
                        ) {
                            showPermissionRationaleDialog = true
                        } else {
                            launcher.launch(permission)
                        }
                    } else {
                        onAction(HomeActions.DownloadImage())
                    }
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
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                .clip(shape = MaterialTheme.shapes.large)
                .clickable { onClick() },
            style = MyTypography.labelLarge.copy(color = Color.White),
            textDecoration = TextDecoration.Underline,
        )
    }
}

@Composable
fun TextBody(text: String) {
    AnimatedTextHome(text) {
        Text(
            text = text,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            style = MyTypography.displayLarge.copy(
                color = Color.White
            )
        )
    }
}

@Composable
fun IconButtonMenu(
    cDescription: String,
    icon: ImageVector,
    color: Color = SecondaryColor,
    isEnabled: Boolean = true,
    shadowOn: Boolean = false,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(contentColor = color),
        modifier = Modifier.padding(end = 4.dp),
        enabled = isEnabled
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (shadowOn)  Box(
                modifier = Modifier.background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                ).size(40.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = cDescription,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}


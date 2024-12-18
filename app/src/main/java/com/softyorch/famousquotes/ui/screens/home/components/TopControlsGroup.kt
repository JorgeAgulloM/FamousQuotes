package com.softyorch.famousquotes.ui.screens.home.components

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.core.commonComponents.IconButtonMenu
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.theme.PrimaryColor
import com.softyorch.famousquotes.ui.theme.SecondaryColor
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.utils.sdk29AndDown
import com.softyorch.famousquotes.utils.showToast

@Composable
fun TopControlsGroup(
    hasText: String,
    isEnabled: Boolean,
    isImageExt: Boolean,
    isShoImage: Boolean,
    disabledReload: Boolean,
    paddingTop: Dp,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onAction: (HomeActions) -> Unit,
) {
    var showPermissionRationaleDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onAction(HomeActions.ImageDownloadRequest())
        } else {
            showPermissionRationaleDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop)
            .zIndex(10f),
        contentAlignment = Alignment.TopEnd
    ) {
        AnimatedTextHome(hasText, !isShoImage) {
            TopControls(
                isEnabled = isEnabled,
                isImageExt = isImageExt,
                disabledReload = disabledReload,
                context = context,
                onAction = onAction,
                onNavigateToUserScreen = onNavigateToUserScreen,
                onNavigateToSettings = onNavigateToSettings,
                onLaunch = { launcher.launch(permission.WRITE_EXTERNAL_STORAGE) }
            ) {
                showPermissionRationaleDialog = true
            }
        }
    }

    if (showPermissionRationaleDialog) BasicDialogApp(
        text = stringResource(R.string.dialog_permission_rationale_text),
        auxText = stringResource(R.string.dialog_permission_rationale_aux_text),
        textBtnPositive = stringResource(R.string.dialog_permission_rationale_denied),
        textBtnNegative = stringResource(R.string.dialog_permission_rationale_ok),
    ) { action ->
        when (action) {
            DialogCloseAction.POSITIVE -> launcher.launch(permission.WRITE_EXTERNAL_STORAGE)
            DialogCloseAction.NEGATIVE -> context.showToast(context.getString(R.string.dialog_permission_rationale_denied_toast))
            DialogCloseAction.DISMISS -> Unit
        }
        showPermissionRationaleDialog = false
    }
}

@Composable
private fun TopControls(
    isEnabled: Boolean,
    isImageExt: Boolean,
    disabledReload: Boolean,
    context: Context,
    onAction: (HomeActions) -> Unit,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLaunch: () -> Unit,
    showPermissionRationaleDialog: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_lost_connection),
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Outlined.WifiOff,
            isVisible = !isEnabled,
            shadowOn = true
        ) { onAction(HomeActions.ShowNoConnectionDialog()) }

        FoldableMenu(
            isEnabled = isEnabled,
            expanded = expanded,
            isImageExt = isImageExt,
            context = context,
            disabledReload = disabledReload,
            onAction = onAction,
            onNavigateToUserScreen = onNavigateToUserScreen,
            onNavigateToSettings = onNavigateToSettings,
            onLaunch = onLaunch,
            showPermissionRationaleDialog = showPermissionRationaleDialog,
            onCloseMenu = { expanded = !expanded }
        )
    }
}

@Composable
private fun FoldableMenu(
    isEnabled: Boolean,
    isImageExt: Boolean,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    context: Context,
    disabledReload: Boolean,
    onAction: (HomeActions) -> Unit,
    onNavigateToUserScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLaunch: () -> Unit,
    showPermissionRationaleDialog: () -> Unit,
    onCloseMenu: () -> Unit
) {
    val permissionState =
        ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)

    val shape = MaterialTheme.shapes.extraLarge
    val closedSize = 48
    val iconsCount = 6
    val expandedSize = closedSize * iconsCount
    val borderStroke = if (expanded) 1.dp else 0.dp
    val shadow = if (expanded) 4.dp else 2.dp

    Column(
        modifier = modifier
            .padding(end = 8.dp)
            .shadow(elevation = shadow, shape = shape)
            .background(SecondaryColor.copy(alpha = 0.6f), shape = shape)
            .border(border = BorderStroke(borderStroke, PrimaryColor), shape = shape)
            .animateContentSize()
            .height(if (expanded) expandedSize.dp else closedSize.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        IconOpenCloseMenu(expanded, onCloseMenu)

        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_other_quote),
            icon = Icons.Outlined.RestartAlt,
            isEnabled = isEnabled && !disabledReload
        ) {
            onCloseMenu()
            onAction(HomeActions.NewQuoteRequest())
        }

        if (isImageExt) IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_buy_image),
            icon = Icons.Outlined.Download,
            isEnabled = isEnabled
        ) {
            onCloseMenu()
            SelectAction(
                permissionState = permissionState,
                context = context,
                showPermissionRationaleDialog = showPermissionRationaleDialog,
                onLaunch = onLaunch,
                onAction = onAction
            )
        }

        IconButtonMenu(
            cDescription = "Go to Grid View",
            icon = Icons.Outlined.GridView,
            isEnabled = isEnabled
        ) {
            onCloseMenu()
            onNavigateToUserScreen()
        }

        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_info),
            icon = Icons.Outlined.Info,
            isEnabled = isEnabled,
        ) {
            onCloseMenu()
            onAction(HomeActions.Info())
        }

        IconButtonMenu(
            cDescription = "Settings",
            icon = Icons.Outlined.Settings,
            isEnabled = isEnabled,
        ) {
            onCloseMenu()
            onNavigateToSettings()
        }
    }
}

@Composable
private fun IconOpenCloseMenu(expanded: Boolean, onCloseMenu: () -> Unit) {

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "animateFloatAsState"
    )

    val icon = if (expanded) Icons.Outlined.Close else Icons.Outlined.Menu

    Box(modifier = Modifier.graphicsLayer { rotationZ = rotation }) {
        IconButtonMenu(
            cDescription = "Close menu",
            icon = icon,
            shadowOn = true,
            isEnabled = true
        ) {
            onCloseMenu()
        }
    }
}

private fun SelectAction(
    permissionState: Int,
    context: Context,
    showPermissionRationaleDialog: () -> Unit,
    onLaunch: () -> Unit,
    onAction: (HomeActions) -> Unit
) {
    if (sdk29AndDown && permissionState != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            showPermissionRationaleDialog()
        } else {
            onLaunch()
        }
    } else {
        onAction(HomeActions.ImageDownloadRequest())
    }
}

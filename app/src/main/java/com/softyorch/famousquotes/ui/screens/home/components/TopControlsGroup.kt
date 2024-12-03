package com.softyorch.famousquotes.ui.screens.home.components

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.utils.sdk29AndDown
import com.softyorch.famousquotes.utils.showToast

@Composable
fun TopControlsGroup(
    hasText: String,
    isEnabled: Boolean,
    isImageExt: Boolean,
    paddingTop: Dp,
    onNavigateToUserScreen: () -> Unit,
    onAction: (HomeActions) -> Unit,
) {
    var showPermissionRationaleDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onAction(HomeActions.DownloadImage())
        } else {
            showPermissionRationaleDialog = true
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(top = paddingTop).zIndex(10f),
        contentAlignment = Alignment.TopEnd
    ) {
        AnimatedTextHome(hasText) {
            TopControls(
                isEnabled = isEnabled,
                isImageExt = isImageExt,
                context = context,
                onAction = onAction,
                onNavigateToUserScreen = onNavigateToUserScreen,
                onLaunch = { launcher.launch(permission.WRITE_EXTERNAL_STORAGE) }
            ) {
                showPermissionRationaleDialog = true
            }
        }
    }

    if (showPermissionRationaleDialog) BasicDialogApp(
        text = stringResource(R.string.dialog_permission_rationale_text),
        auxText = stringResource(R.string.dialog_permission_rationale_aux_text),
        textBtnPositive = stringResource(R.string.dialog_permission_rationale_ok),
        textBtnNegative = stringResource(R.string.dialog_permission_rationale_denied),
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
    context: Context,
    onAction: (HomeActions) -> Unit,
    onNavigateToUserScreen: () -> Unit,
    onLaunch: () -> Unit,
    showPermissionRationaleDialog: () -> Unit
) {
    val permissionState = ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonMenu(
            cDescription = stringResource(R.string.main_icon_content_desc_lost_connection),
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Outlined.WifiOff,
            isVisible = !isEnabled,
            shadowOn = true
        ) { onAction(HomeActions.ShowNoConnectionDialog()) }
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
                        permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    showPermissionRationaleDialog()
                } else {
                    onLaunch()
                }
            } else {
                onAction(HomeActions.DownloadImage())
            }
        }

        IconButtonMenu(
            cDescription = "User Space",
            icon = Icons.Outlined.GridView,
            shadowOn = true,
            isEnabled = isEnabled
        ) {
            onNavigateToUserScreen()
        }
    }
}

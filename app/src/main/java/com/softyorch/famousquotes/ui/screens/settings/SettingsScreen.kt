package com.softyorch.famousquotes.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.ui.core.commonComponents.AppVersionText
import com.softyorch.famousquotes.ui.core.commonComponents.BasicDialogApp
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.core.commonComponents.SpacerIconButton
import com.softyorch.famousquotes.ui.core.commonComponents.TopBarStandard
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.ButtonApp
import com.softyorch.famousquotes.ui.screens.home.components.HeaderSubtitleApp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.screens.home.components.SpacerWidth
import com.softyorch.famousquotes.ui.theme.AppColorSchema
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.ui.utils.extFunc.copyToClipboard
import com.softyorch.famousquotes.utils.userId

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onBackNavigation: () -> Unit
) {
    val paddingTop = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp() + 24.dp
    }

    LaunchedEffect(Unit) {
        viewModel.actions(SettingsActions.GetSettings())
    }

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnResume {
        if (state.notificationsPermissionState == NotificationsPermissionState.Waiting)
            viewModel.actions(SettingsActions.NotificationChannel(notificationChannel = true))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarStandard(
                modifier = modifier,
                paddingTop = paddingTop,
                leftHanded = settings.leftHanded,
                textTitle = stringResource(R.string.settings_screen_top_bar_title),
                iconTitle = Icons.Default.Settings,
                onUpNavigation = onBackNavigation
            )
        },
        containerColor = AppColorSchema.background
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)
        ) {
            SettingsCheckers(
                settings = settings,
                onActions = viewModel::actions
            )
            SettingsButtons(
                onNavigateToOnBoarding = onNavigateToOnBoarding,
                onNavigateToInfo = onNavigateToInfo
            )
        }

        if (state.isLoading) LoadingCircle()

        PrepareDialogs(state = state.notificationsPermissionState, onActions = viewModel::actions)

    }
}

@Composable
fun SettingsCheckers(
    modifier: Modifier = Modifier,
    settings: SettingsModel,
    onActions: (SettingsActions) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        SwitchSettings(
            isChecked = settings.autoDarkMode,
            isEnable = true,
            isLeftHanded = settings.leftHanded,
            titleText = stringResource(R.string.settings_screen_switch_auto_dark_mode_title),
            descriptionText = stringResource(R.string.settings_screen_switch_auto_dark_mode_description)
        ) { onActions(SettingsActions.AutoDarkMode(autoDarkMode = it)) }

        SwitchSettings(
            isChecked = settings.darkMode,
            isEnable = !settings.autoDarkMode,
            isLeftHanded = settings.leftHanded,
            titleText = stringResource(R.string.settings_screen_switch_dark_mode_title),
            descriptionText = stringResource(R.string.settings_screen_switch_dark_mode_description)
        ) { onActions(SettingsActions.DarkMode(darkMode = it)) }

        SwitchSettings(
            isChecked = settings.leftHanded,
            isEnable = true,
            isLeftHanded = settings.leftHanded,
            titleText = stringResource(R.string.settings_screen_switch_left_handed_title),
            descriptionText = stringResource(R.string.settings_screen_switch_left_handed_description)
        ) { onActions(SettingsActions.LeftHanded(leftHanded = it)) }

        SwitchSettings(
            isChecked = settings.notificationChannel,
            isEnable = true,
            isLeftHanded = settings.leftHanded,
            titleText = stringResource(R.string.settings_screen_switch_notification_channel_title),
            descriptionText = stringResource(R.string.settings_screen_switch_notification_channel_description)
        ) { onActions(SettingsActions.NotificationChannel(notificationChannel = it)) }
    }
}

@Composable
fun SettingsButtons(
    modifier: Modifier = Modifier,
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToInfo: () -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val modifierBtn = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
        SpacerHeight()
        ButtonApp(
            modifier = modifierBtn,
            text = stringResource(R.string.settings_screen_btn_on_boarding_text),
            primary = true
        ) { onNavigateToOnBoarding() }
        ButtonApp(
            modifier = modifierBtn,
            text = stringResource(R.string.settings_screen_btn_info_softyorch_text)
        ) { onNavigateToInfo() }
        SpacerHeight()
        IdDeviceButton(modifier, context)

        Column(modifier = modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
            AppIcon()
            HeaderSubtitleApp()
            AppVersionText {}
            SpacerIconButton()
            SpacerHeight(32)
        }
    }
}

@Composable
private fun IdDeviceButton(modifier: Modifier, context: Context) {

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                showDialog = true
                context.copyToClipboard(
                    context.getString(R.string.settings_screen_clip_board_title_copy_text),
                    context.userId()
                )
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Android,
            contentDescription = stringResource(R.string.settings_screen_icon_btn_id_device_text),
            tint = AppColorSchema.secondary
        )
        SpacerWidth()
        Text(
            text = stringResource(R.string.settings_screen_text_btn_title),
            style = MaterialTheme.typography.labelLarge.copy(
                color = AppColorSchema.text
            ),
            textDecoration = TextDecoration.Underline
        )
    }

    if (showDialog) BasicDialogApp(
        text = stringResource(R.string.settings_screen_dialog_id_text),
        auxText = stringResource(R.string.settings_screen_dialog_id_aux_text),
        title = stringResource(R.string.settings_screen_dialog_id_title),
        textBtnNegative = null,
        textBtnPositive = null,
        blockDismissActions = true
    ) {
        showDialog = false
    }
}

@Composable
fun OnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun PrepareDialogs(
    state: NotificationsPermissionState,
    onActions: (SettingsActions) -> Unit
) {
    if (state == NotificationsPermissionState.Denied)
        BasicDialogApp(
            text = stringResource(R.string.settings_screen_dialog_permission_text),
            auxText = stringResource(R.string.settings_screen_dialog_permission_aux_text),
            title = stringResource(R.string.settings_screen_dialog_permission_title),
            textBtnNegative = stringResource(R.string.settings_screen_dialog_permission_btn_negative_text),
            textBtnPositive = stringResource(R.string.settings_screen_dialog_permission_btn_positive_text),
            blockDismissActions = true
        ) { result ->
            when (result) {
                DialogCloseAction.NEGATIVE -> onActions(SettingsActions.NotificationsPermissionUserWantBlock())
                DialogCloseAction.POSITIVE -> onActions(SettingsActions.NotificationsPermissionRequest())
                DialogCloseAction.DISMISS -> Unit
            }
        }

    if (state == NotificationsPermissionState.Blocked)
        BasicDialogApp(
            text = stringResource(R.string.settings_screen_dialog_permission_assert_text),
            auxText = stringResource(R.string.settings_screen_dialog_permission_assert_aux_text),
            title = stringResource(R.string.settings_screen_dialog_permission_assert_title),
            textBtnNegative = stringResource(R.string.settings_screen_dialog_permission_assert_btn_negative_text),
            textBtnPositive = stringResource(R.string.settings_screen_dialog_permission_assert_btn_positive_text),
            blockDismissActions = true
        ) { result ->
            when (result) {
                DialogCloseAction.NEGATIVE -> onActions(SettingsActions.NotificationsPermissionUserBlocked())
                DialogCloseAction.POSITIVE -> onActions(SettingsActions.NotificationsPermissionRequest())
                DialogCloseAction.DISMISS -> Unit
            }
        }
}

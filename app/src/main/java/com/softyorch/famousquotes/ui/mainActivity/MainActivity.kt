package com.softyorch.famousquotes.ui.mainActivity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.navigation.NavigationManager
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.RequestGrantedProtectionData
import com.softyorch.famousquotes.utils.sdk26AndUp
import com.softyorch.famousquotes.utils.sdk33AndUp
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var appUpdateManager: AppUpdateManager
    private val appUpdateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
    private val channel = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (!BuildConfig.DEBUG) checkForAppUpdates()

        // Permission Notifications
        sdk33AndUp { PermissionNotifications() }

        // Protection Data Consent
        val requestConsent = RequestGrantedProtectionData(this)

        // ScreenShoot blocked
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            FamousQuotesTheme {
                requestConsent.get { }

                viewModel = hiltViewModel<MainViewModel>()

                val state: MainState by viewModel.mainState.collectAsStateWithLifecycle()

                when (state) {
                    MainState.Home -> NavigationManager()
                    MainState.TimeToUpdate -> MainAlertDialog { alertState ->
                        when (alertState) {
                            AlertState.Dismiss -> finish()
                            AlertState.Update -> viewModel.goToUpdateApp()
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun PermissionNotifications() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                writeLog(LevelLog.INFO, "PERMISSION POST_NOTIFICATIONS GRANTED")
                sdk26AndUp { CreatedChannelNotifications() }
            } else {
                writeLog(LevelLog.WARN, "PERMISSION POST_NOTIFICATIONS DENIED")
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CreatedChannelNotifications() {
        val channelId = getString(R.string.default_channel)
        val chName = "${BuildConfig.APP_TITLE}_Promotion"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, chName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onResume() {
        super.onResume()
        if (!BuildConfig.DEBUG) appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(info, this, appUpdateOptions, channel)
            }
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isImmediateUpdateAllowed
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(info, this, appUpdateOptions, channel)
            }
        }
    }
}

//Añadir Tests de implementación
//Añadir CD/CI con github

//Crear capturas, videos, textos... y subir apps

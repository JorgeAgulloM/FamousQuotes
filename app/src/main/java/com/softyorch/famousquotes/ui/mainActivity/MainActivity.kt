package com.softyorch.famousquotes.ui.mainActivity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.screens.MainApp
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.RequestGrantedProtectionData
import com.softyorch.famousquotes.utils.sdk26AndUp
import com.softyorch.famousquotes.utils.sdk30AndDown
import com.softyorch.famousquotes.utils.sdk33AndUp
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var instance: MainActivity
        fun getPermissionStorage(isGranted: (Boolean) -> Unit) =
            instance.getPermissionStorage { isGranted(it) }
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val appUpdateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
    private val channel = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        instance = this

        splash.setKeepOnScreenCondition { true }

        StartUpdateManager()
        StartFirebaseCrashlytics()
        sdk33AndUp { PermissionNotifications() }
        RequestGrantedProtectionData(this).getConsent()
        SetBlockedScreenShoot()
        SetLauncherPermissions()
        getPermissionStorage { }

        setContent {
            viewModel = hiltViewModel<MainViewModel>()
            val state: MainState by viewModel.mainState.collectAsStateWithLifecycle()

            if (state == MainState.TimeToUpdate) MainAlertDialog { alertState ->
                when (alertState) {
                    AlertState.Dismiss -> finish()
                    AlertState.Update -> viewModel.goToUpdateApp()
                }
            }

            MainApp()
            splash.setKeepOnScreenCondition { false }
        }
    }

    private fun StartUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (!BuildConfig.DEBUG) checkForAppUpdates()
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

    private fun StartFirebaseCrashlytics() {
        // Start Firebase Crashlytics
        FirebaseApp.initializeApp(this)
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    private fun SetBlockedScreenShoot() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    private fun SetLauncherPermissions() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                writeLog(LevelLog.INFO, "Storage permission granted")
            } else {
                writeLog(LevelLog.WARN, "Storage permission denied")
            }
        }
    }

    private fun getPermissionStorage(isGranted: (Boolean) -> Unit) {
        sdk30AndDown {
            when {
                ContextCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED -> isGranted(true)

                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ->
                    writeLog(LevelLog.INFO, "shouldShowRequestPermissionRationale")
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    writeLog(LevelLog.INFO, "requestPermissionLauncher")
                }
            }
        } ?: isGranted(true)
    }
}

//Añadir Tests de implementación
//Añadir CD/CI con github

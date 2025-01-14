package com.softyorch.famousquotes.ui.mainActivity

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.core.NotificationUtils
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.screens.main.MainApp
import com.softyorch.famousquotes.ui.screens.main.MainViewModel
import com.softyorch.famousquotes.utils.RequestGrantedProtectionData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var instance: MainActivity
        var packageAppName: String = ""
    }

    private lateinit var viewActivityModel: MainActivityViewModel
    private lateinit var appUpdateManager: AppUpdateManager
    private val appUpdateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
    private val channel = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        instance = this
        packageAppName = applicationContext.packageName

        enableEdgeToEdge()

        splash.setKeepOnScreenCondition { true }

        StartUpdateManager()
        StartFirebase()
        NotificationUtils(this).verifyPermissionNotifications(true) { subscried ->
            if (subscried) viewActivityModel.setSubscribeNotificationsSetting()
        }
        RequestGrantedProtectionData(this).getConsent()
        SetBlockedScreenShoot()

        setContent {
            viewActivityModel = hiltViewModel<MainActivityViewModel>()
            val state: MainState by viewActivityModel.mainState.collectAsStateWithLifecycle()
            val mainViewModel = hiltViewModel<MainViewModel>()

            when (state) {
                MainState.Home -> MainApp(viewModel = mainViewModel) { splash.setKeepOnScreenCondition { false } }
                MainState.Unauthorized -> LoadingCircle()
                MainState.Start -> Unit
            }
        }
    }

    private fun StartUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (!BuildConfig.DEBUG) checkForAppUpdates()
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

    private fun StartFirebase() {
        FirebaseApp.initializeApp(this)

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Firebase.crashlytics.recordException(throwable)
        }
    }

    private fun SetBlockedScreenShoot() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}

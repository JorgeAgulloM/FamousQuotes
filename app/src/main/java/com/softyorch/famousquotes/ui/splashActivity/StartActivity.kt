package com.softyorch.famousquotes.ui.splashActivity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : ComponentActivity() {

    private lateinit var viewModel: StartViewModel
    private lateinit var appUpdateManager: AppUpdateManager
    private val appUpdateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
    private val channel = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)

        screenSplash.setKeepOnScreenCondition { true }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (!BuildConfig.DEBUG) checkForAppUpdates()

        // ScreenShoot blocked
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            viewModel = hiltViewModel<StartViewModel>()

            val uiState: StartState by viewModel.uiState.collectAsStateWithLifecycle()

            when (uiState) {
                StartState.Loading -> {}
                StartState.Start -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                StartState.TimeToUpdate -> MainAlertDialog { alertState ->
                    when (alertState) {
                        AlertState.Dismiss -> finish()
                        AlertState.Update -> viewModel.goToUpdateApp()
                    }
                }
            }
        }
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

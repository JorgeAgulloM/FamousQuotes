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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.home.HomeScreen
import com.softyorch.famousquotes.ui.home.HomeViewModel
import com.softyorch.famousquotes.ui.theme.BackgroundColor
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.RequestGrantedProtectionData
import com.softyorch.famousquotes.utils.sdk26AndUp
import com.softyorch.famousquotes.utils.sdk33AndUp
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Protection Data Consent
        val requestConsent = RequestGrantedProtectionData(this)

        // Permission Notifications
        sdk33AndUp { PermissionNotifications() }

        // ScreenShoot blocked
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            FamousQuotesTheme {
                requestConsent.get { }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                     HomeScreen(hiltViewModel<HomeViewModel>())
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
}

//Añadir Tests de implementación
//Añadir CD/CI con github

//Crear capturas, videos, textos... y subir apps

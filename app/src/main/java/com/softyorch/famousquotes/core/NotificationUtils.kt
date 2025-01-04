package com.softyorch.famousquotes.core

import android.Manifest
import android.app.Activity.NOTIFICATION_SERVICE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.sdk33AndUp
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationUtils(private val context: Context) {

    fun verifyPermissionNotifications(
        request: Boolean,
        onResult: (Boolean) -> Unit = {}
    ) {
        sdk33AndUp {
            if (request) {
                val requestPermissionLauncher: ActivityResultLauncher<String> =
                    MainActivity.instance.registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            writeLog(LevelLog.INFO, "PERMISSION POST_NOTIFICATIONS GRANTED")
                            createdChannelNotifications()
                            startProcessSubscribeNotifications()
                        } else {
                            writeLog(LevelLog.WARN, "PERMISSION POST_NOTIFICATIONS DENIED")
                        }
                    }

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val permissionStatus = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )

                when (permissionStatus) {
                    PackageManager.PERMISSION_GRANTED -> {
                        writeLog(LevelLog.INFO, "PERMISSION POST_NOTIFICATIONS GRANTED CONFIRMED")
                        onResult(true)
                    }

                    PackageManager.PERMISSION_DENIED -> {
                        writeLog(LevelLog.WARN, "PERMISSION POST_NOTIFICATIONS DENIED CONFIRMED")
                        onResult(false)
                    }
                }
            }
        } ?: CoroutineScope(Dispatchers.Default).launch {
            subscribeNotificationFirstTime().also { onResult(true) }
        }
    }

    private fun createdChannelNotifications() {
        sdk33AndUp {
            val channelId = context.getString(R.string.default_channel)
            val chName = "${BuildConfig.APP_TITLE}_Promotion"
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val channel =
                NotificationChannel(channelId, chName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startProcessSubscribeNotifications() {
        areNotificationsEnabled { result ->
            if (result) CoroutineScope(Dispatchers.Default).launch {
                subscribeNotificationFirstTime()
            }
        }
    }

    private fun areNotificationsEnabled(onResult: (Boolean) -> Unit) {
        val notificationManager = NotificationManagerCompat.from(context)
        onResult(notificationManager.areNotificationsEnabled())
    }

    private suspend fun subscribeNotificationFirstTime() {
        var finished = false
        val delay = 5000L
        var retry = 0
        val messaging = FirebaseMessaging.getInstance()

        while (!finished) {
            messaging.subscribeToTopic(FIREBASE_NOTIFICATION_CHANNEL_1)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        writeLog(text = "Subscribed to $FIREBASE_NOTIFICATION_CHANNEL_1")
                        finished = true
                    } else {
                        writeLog(
                            LevelLog.ERROR,
                            text = "Error subscribing to $FIREBASE_NOTIFICATION_CHANNEL_1"
                        )
                        writeLog(LevelLog.ERROR, text = "Times $retry. Retry in 5 seconds...")
                        finished = retry >= 3
                        retry += 1
                    }
                }.addOnFailureListener {
                    writeLog(
                        LevelLog.ERROR,
                        text = "Error subscribing to $FIREBASE_NOTIFICATION_CHANNEL_1"
                    )
                    writeLog(LevelLog.ERROR, text = "Times $retry. Retry in 5 seconds...")
                    finished = retry >= 3
                    retry += 1
                }
            delay(delay)
        }
    }

    fun goToConfigurationNotifications() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }
}
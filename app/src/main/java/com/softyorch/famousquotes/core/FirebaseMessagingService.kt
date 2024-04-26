package com.softyorch.famousquotes.core


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Inject

class FirebaseMessagingService @Inject constructor() : FirebaseMessagingService() {

    private val code = 999

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToken(token)
    }

    private fun sendRegistrationToken(token: String) {
        writeLog(level = LevelLog.DEBUG, text = "Firebase token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            createNotification()
        }
    }

    private fun createNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent =
            PendingIntent.getActivity(this, code, intent, PendingIntent.FLAG_IMMUTABLE)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val channelId = getString(R.string.default_channel)

        val title = getString(R.string.fbm_message_title)
        val body = getString(R.string.fbm_message_body)
        val icon = this.getResourceDrawableIdentifier(BuildConfig.ICON) ?: R.drawable.default_icon

        val notificationBuilder = NotificationCompat.Builder(this, "")
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}

package com.softyorch.famousquotes.ui.mainActivity

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.google.firebase.messaging.FirebaseMessaging
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.core.FIREBASE_NOTIFICATION_CHANNEL_1
import com.softyorch.famousquotes.ui.core.commonComponents.LoadingCircle
import com.softyorch.famousquotes.ui.screens.main.MainApp
import com.softyorch.famousquotes.ui.utils.DialogCloseAction
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.RequestGrantedProtectionData
import com.softyorch.famousquotes.utils.sdk33AndUp
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var instance: MainActivity
        var packageAppName: String = ""
    }

    private lateinit var viewModel: MainViewModel
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
        PermissionNotifications()
        RequestGrantedProtectionData(this).getConsent()
        SetBlockedScreenShoot()

        setContent {
            viewModel = hiltViewModel<MainViewModel>()
            val state: MainState by viewModel.mainState.collectAsStateWithLifecycle()

            when (state) {
                MainState.Home -> MainApp { splash.setKeepOnScreenCondition { false } }
                MainState.Unauthorized -> LoadingCircle()
                MainState.Start -> Unit
            }
        }
    }

    private fun StartUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (!BuildConfig.DEBUG) checkForAppUpdates()
    }

    fun PermissionNotifications(fromSettings: Boolean = false, onResult: (Boolean) -> Unit = {}) {
            sdk33AndUp {
                if (fromSettings) {
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )

                    when (permissionStatus) {
                        PackageManager.PERMISSION_GRANTED -> {
                            writeLog(LevelLog.INFO, "PERMISSION POST_NOTIFICATIONS GRANTED")
                            onResult(true)
                        }

                        PackageManager.PERMISSION_DENIED -> {
                            writeLog(LevelLog.WARN, "PERMISSION POST_NOTIFICATIONS DENIED")
                            onResult(false)
                        }
                    }
                } else {
                    val requestPermissionLauncher = registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            writeLog(LevelLog.INFO, "PERMISSION POST_NOTIFICATIONS GRANTED")
                            CreatedChannelNotifications()
                            StartProcessSuscribeNotifications()
                        } else {
                            writeLog(LevelLog.WARN, "PERMISSION POST_NOTIFICATIONS DENIED")
                        }
                    }

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } ?: SubscribeNotificationFirstTime().also { onResult(true) }
    }

    private fun StartProcessSuscribeNotifications() {
        AreNotificationsEnabled { result ->
            if (result) SubscribeNotificationFirstTime()
            else ShowDialogNotificationsDisabled()
        }
    }

    private fun AreNotificationsEnabled(onResult: (Boolean) -> Unit) {
        val notificationManager = NotificationManagerCompat.from(this)
        onResult(notificationManager.areNotificationsEnabled())
    }

    private fun CreatedChannelNotifications() {
        val channelId = getString(R.string.default_channel)
        val chName = "${BuildConfig.APP_TITLE}_Promotion"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, chName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    private fun SubscribeNotificationFirstTime() {
        var finished = false
        val delay = 5000L
        var retry = 0
        val messaging = FirebaseMessaging.getInstance()

        lifecycleScope.launch {
            while (!finished) {
                messaging.subscribeToTopic(FIREBASE_NOTIFICATION_CHANNEL_1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        writeLog(text = "Subscribed to $FIREBASE_NOTIFICATION_CHANNEL_1")
                        finished = true
                    } else {
                        writeLog(LevelLog.ERROR, text = "Error subscribing to $FIREBASE_NOTIFICATION_CHANNEL_1")
                        writeLog(LevelLog.ERROR, text = "Times $retry. Retry in 5 seconds...")
                        finished = retry >= 3
                        retry += 1
                    }
                }.addOnFailureListener {
                    writeLog(LevelLog.ERROR, text = "Error subscribing to $FIREBASE_NOTIFICATION_CHANNEL_1")
                    writeLog(LevelLog.ERROR, text = "Times $retry. Retry in 5 seconds...")
                    finished = retry >= 3
                    retry += 1
                }
                delay(delay)
            }
        }
    }

    private fun ShowDialogNotificationsDisabled() {
        //Esto no sirve, hay que lanzad un dialogo standar.
        showDialog(
            title = "Notificaciones deshabilitadas",
            message = "Las notificaciones están deshabilitadas para esta aplicación. Por favor, actívalas en la configuración del sistema para recibir actualizaciones importantes.",
            positiveButtonText = "Ir a configuración",
            negativeButtonText = "Cancelar"
        ) { action ->
            when (action) {
                DialogCloseAction.POSITIVE -> GoToConfigurationNotifications()
                DialogCloseAction.NEGATIVE -> ShowConfirmDeniedDialog()
                DialogCloseAction.DISMISS -> Unit
            }
        }
    }

    private fun GoToConfigurationNotifications() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    private fun ShowConfirmDeniedDialog() {
        showDialog(
            title = "Confirmar bloqueo de notificaciones",
            message = "Entendemos que hayas decidido no activar las notificaciones en este momento. Ten en cuenta que no recibirás alertas ni actualizaciones importantes relacionadas con la aplicación. Si cambias de opinión, siempre podrás habilitarlas más adelante desde la configuración del sistema.",
            positiveButtonText = "Confirmar",
            negativeButtonText = "Vale, quiero activarlas"
        ) { action ->
            when (action) {
                DialogCloseAction.POSITIVE -> Unit
                DialogCloseAction.NEGATIVE -> GoToConfigurationNotifications()
                DialogCloseAction.DISMISS -> Unit
            }
        }
    }

    private fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onAction: (DialogCloseAction) -> Unit
    ) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onAction(DialogCloseAction.POSITIVE)
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                onAction(DialogCloseAction.NEGATIVE)
                dialog.dismiss()
            }
            .create()

        dialog.show()
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
        // Start Firebase
        FirebaseApp.initializeApp(this)

        // Start Firebase Analytics
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)

        // Start Firebase Crashlytics
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        // Configurar Crashlytics para manejar excepciones no capturadas
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

//Añadir Tests de implementación
//Añadir CD/CI con github

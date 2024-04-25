package com.softyorch.famousquotes.ui.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.home.HomeScreen
import com.softyorch.famousquotes.ui.home.HomeViewModel
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme
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

        setContent {
            FamousQuotesTheme {
                viewModel = hiltViewModel<MainViewModel>()

                val state: MainState by viewModel.mainState.collectAsStateWithLifecycle()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (state) {
                        MainState.Home -> HomeScreen(hiltViewModel<HomeViewModel>())
                        MainState.TimeToUpdate -> MainAlertDialog { alertState ->
                            when (alertState) {
                                AlertState.Dismiss -> finish()
                                AlertState.Update -> viewModel.goToUpdateApp()
                            }
                        }

                        else -> {}
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


//Implementar actualziaciones automáticas
//Preparar recepción de notificaciones
//Implementar "Like" + recuento + lista de likes????????
//Traducir las frases que ya tengo
//Generar imagenes
//Generar script python para cargar frases a firebase
//Añadir Tests unitarios
//Añadir Tests de implementación
//Añadir CD/CI con github

//Crear capturas, videos, textos... y subir apps

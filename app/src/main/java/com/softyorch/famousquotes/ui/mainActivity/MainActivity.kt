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
import com.softyorch.famousquotes.ui.home.HomeScreen
import com.softyorch.famousquotes.ui.home.HomeViewModel
import com.softyorch.famousquotes.ui.theme.FamousQuotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

package com.softyorch.famousquotes.ui.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Intents
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.useCases.GetDbVersion
import com.softyorch.famousquotes.domain.useCases.TimeToUpdate
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDbVersion: GetDbVersion,
    private val timeToUpdate: TimeToUpdate,
    private val storageService: IStorageService,
    private val intents: Intents,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _timeToUpdate = MutableStateFlow<MainState>(MainState.Loading)
    val mainState: StateFlow<MainState> = _timeToUpdate

    init {
        isTimeToUpdate()
        getDbVersionFromService()
    }

    fun goToUpdateApp() {
        viewModelScope.launch {
            intents.goToUpdateInGooglePlay()
        }
    }

    private fun isTimeToUpdate() {
        viewModelScope.launch {
            val needUpdateApp = withContext(dispatcherIO) {
                timeToUpdate()
            }
            writeLog(LevelLog.INFO, "Is time to Update?: $needUpdateApp")
            _timeToUpdate.update {
                if (needUpdateApp) MainState.TimeToUpdate else MainState.Home
            }
        }
    }

    private fun getDbVersionFromService() {
        viewModelScope.launch {
            val needUpdateImageStorage = withContext(dispatcherIO) {
                getDbVersion()
            }

            if (needUpdateImageStorage) {
                getNewDataFormStorageService()
            }
        }
    }

    private fun getNewDataFormStorageService() {
        viewModelScope.launch(dispatcherIO) {
            storageService.getImages()
        }
    }
}

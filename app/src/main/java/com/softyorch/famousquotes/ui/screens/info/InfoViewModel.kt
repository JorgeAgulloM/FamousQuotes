package com.softyorch.famousquotes.ui.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.core.Intents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val intents: Intents,
    private val dispatcherDefault: CoroutineDispatcher
): ViewModel() {

    fun actions(action: InfoActions) {
        when (action) {
            InfoActions.Support -> useIntentSupport()
            InfoActions.BuyMeACoffee -> useIntentBuyMeACoffee()
            InfoActions.CoffeeWithPayPal -> useIntentCoffeeWithPayPal()
            InfoActions.OpenGooglePlay -> useIntentGoGooglePlay()
        }
    }

    private fun useIntentSupport() {
        viewModelScope.launch(dispatcherDefault) {
            intents.goToSupportEmail()
        }
    }

    private fun useIntentBuyMeACoffee() {
        viewModelScope.launch(dispatcherDefault) {
            intents.goToBuyMeACoffee()
        }
    }

    private fun useIntentCoffeeWithPayPal() {
        viewModelScope.launch(dispatcherDefault) {
            intents.goToCoffeeWithPayPal()
        }
    }

    private fun useIntentGoGooglePlay() {
        viewModelScope.launch(dispatcherDefault) {
            intents.goToGooglePlay()
        }
    }

}
package com.softyorch.famousquotes.ui.screens.onboading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.famousquotes.domain.useCases.settings.SetShownOnBoarding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setShownOnBoarding: SetShownOnBoarding
) : ViewModel() {

    fun setOnBoarding() {
        viewModelScope.launch {
            setShownOnBoarding.invoke(onboarding = true)
        }
    }
}

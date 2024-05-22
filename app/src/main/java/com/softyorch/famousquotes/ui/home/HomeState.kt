package com.softyorch.famousquotes.ui.home

import com.softyorch.famousquotes.domain.model.FamousQuoteModel

data class HomeState(
    val quote: FamousQuoteModel,
    val showInfo: Boolean = false,
    val showImage: Boolean = false,
    val showInterstitial: Boolean = false,
    val isLoading: Boolean = false,
    val hasConnection: Boolean? = null,
    val showDialogNoConnection: Boolean? = null,
    val reConnect: Boolean? = null,
)

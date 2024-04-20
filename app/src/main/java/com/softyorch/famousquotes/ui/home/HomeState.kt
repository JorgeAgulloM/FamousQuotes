package com.softyorch.famousquotes.ui.home

import com.softyorch.famousquotes.domain.model.FamousQuoteModel

data class HomeState(
    val quote: FamousQuoteModel,
    val isLoading: Boolean = false,
)

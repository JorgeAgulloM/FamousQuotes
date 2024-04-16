package com.softyorch.famousquotes.ui.home

import com.softyorch.famousquotes.domain.model.FamousQuoteModel

data class HomeState(
    val quote: FamousQuoteModel,
    val image: String = "",
    val isLoading: Boolean = false,
)

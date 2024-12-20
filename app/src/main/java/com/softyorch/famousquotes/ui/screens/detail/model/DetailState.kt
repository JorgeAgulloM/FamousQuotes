package com.softyorch.famousquotes.ui.screens.detail.model

data class DetailState(
    val isLoading: Boolean = false,
    val shareAs: Boolean = false,
    val hasConnection: Boolean = false,
    val showDialogNoConnection: Boolean = false
)

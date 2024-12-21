package com.softyorch.famousquotes.ui.screens.grid

sealed interface GridActions {
    data class LoadingQuotes(val action: String = "LoadingQuotes") : GridActions
    data class AscendingOrder(val field: String = "AscendingOrder") : GridActions
    data class DescendingOrder(val field: String = "DescendingOrder") : GridActions
}

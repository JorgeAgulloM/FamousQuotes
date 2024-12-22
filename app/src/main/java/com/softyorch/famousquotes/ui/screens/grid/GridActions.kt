package com.softyorch.famousquotes.ui.screens.grid

import com.softyorch.famousquotes.core.FilterQuotes

sealed interface GridActions {
    data class LoadingQuotes(val action: String = "LoadingQuotes") : GridActions
    data class SelectFilterQuotes(val action: String = "SelectFilterQuotes", val filterQuotes: FilterQuotes) : GridActions
    data class AscendingOrder(val field: String = "AscendingOrder") : GridActions
    data class DescendingOrder(val field: String = "DescendingOrder") : GridActions
}

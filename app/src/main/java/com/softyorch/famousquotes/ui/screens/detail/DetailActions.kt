package com.softyorch.famousquotes.ui.screens.detail

sealed interface DetailActions {
    data class LoadQuoteData(val action: String = "LoadQuoteData") : DetailActions
    data class SetLikeQuote(val action: String = "SetLikeQuote") : DetailActions
    data class SetFavoriteQuote(val action: String = "SetFavoriteQuote") : DetailActions
    data class DownloadQuote(val action: String = "DownloadQuote") : DetailActions
    data class HowToShareQuote(val action: String = "HowToShareQuote") : DetailActions
    data class ShareQuoteAs(val action: String = "ShareQuoteAsText", val shareAs: ShareAs) : DetailActions
    data class OwnerQuoteIntent(val action: String = "OwnerQuoteIntent") : DetailActions
    data class ShowNoConnectionDialog(val action: String = "OwnerQuoteIntent") : DetailActions
    data class HideControls(val action: String = "HideControls") : DetailActions
}

package com.softyorch.famousquotes.ui.screens.detail

sealed interface DetailActions {
    data class LoadQuoteData(val action: String = "LoadQuoteData") : DetailActions
    data class SetLikeQuote(val action: String = "SetLikeQuote") : DetailActions
    data class SetFavoriteQuote(val action: String = "SetFavoriteQuote") : DetailActions
    data class DownloadQuote(val action: String = "DownloadQuote") : DetailActions
    data class ShareQuoteAsImage(val action: String = "ShareQuoteAsImage") : DetailActions
    data class ShareQuoteAsText(val action: String = "ShareQuoteAsText") : DetailActions
}

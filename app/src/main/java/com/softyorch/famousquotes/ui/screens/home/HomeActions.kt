package com.softyorch.famousquotes.ui.screens.home

sealed class HomeActions {
    data class Info(val action: String = "Info"): HomeActions()
    data class New(val action: String = "New"): HomeActions()
    data class ShareWithImage(val action: String = "ShareWithImage"): HomeActions()
    data class ShareText(val action: String = "ShareText"): HomeActions()
    data class Owner(val action: String = "Owner"): HomeActions()
    data class Like(val action: String = "Like"): HomeActions()
    data class ShowImage(val action: String = "ShowImage"): HomeActions()
    data class ShowNoConnectionDialog(val action: String = "ShowNoConnectionDialog"): HomeActions()
    data class ReConnection(val action: String = "ReConnection"): HomeActions()
    data class DownloadImage(val action: String = "DownloadImage"): HomeActions()
    data class DownloadImageByBonifiedAd(val action: String = "ViewBonifiedAds"): HomeActions()
    data class ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd(val action: String = "ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd"): HomeActions()
    data class ShowToastDownload(val action: String = "ShowToastDownload"): HomeActions()
    data class CloseDialogDownLoadImageAgain(val action: String = "CloseDialogDownLoadImageAgain"): HomeActions()
    data class SureDownloadImageAgain(val action: String = "SureDownloadImageAgain"): HomeActions()
}

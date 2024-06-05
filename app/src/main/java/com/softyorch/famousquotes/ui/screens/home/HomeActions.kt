package com.softyorch.famousquotes.ui.screens.home

sealed class HomeActions(val select: String) {
    data class Info(val action: String = "Info"): HomeActions(action)
    data class New(val action: String = "New"): HomeActions(action)
    data class Send(val action: String = "Send"): HomeActions(action)
    data class Buy(val action: String = "Buy"): HomeActions(action)
    data class Owner(val action: String = "Owner"): HomeActions(action)
    data class Like(val action: String = "Like"): HomeActions(action)
    data class ShowImage(val action: String = "ShowImage"): HomeActions(action)
    data class ShowNoConnectionDialog(val action: String = "ShowNoConnectionDialog"): HomeActions(action)
    data class ReConnection(val action: String = "ReConnection"): HomeActions(action)
    data class DownloadImage(val action: String = "DownloadImage"): HomeActions(action)
    data class ShowToastDownload(val action: String = "ShowToastDownload"): HomeActions(action)
    data class CloseDialogDownLoadImageAgain(val action: String = "CloseDialogDownLoadImageAgain"): HomeActions(action)
    data class SureDownloadImageAgain(val action: String = "SureDownloadImageAgain"): HomeActions(action)
}

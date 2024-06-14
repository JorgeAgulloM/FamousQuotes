package com.softyorch.famousquotes.core

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.mainActivity.MainActivity.Companion.firebaseAnalytics
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.utils.writeLog

sealed class Analytics(val name: String) {
    data class Action(val id: HomeActions) : Analytics(actionName(id))
    data class Banner(val id: String = "ad_banner") : Analytics(id)
    data class Interstitial(val id: String = "ad_interstitial") : Analytics(id)

    companion object {
        fun sendAction(action: Analytics) {
            val bundle = Bundle()
            bundle.putString(action.name, "clicked")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            writeLog(text = "send action to Analytics: ${action.name}")
            writeLog(text = "Package Name: ${MainActivity.instance.packageName}")
        }

        private const val FLAVOR = BuildConfig.FLAVOR

        private fun actionName(action: HomeActions) = when (action) {
            is HomeActions.Buy -> "${FLAVOR}_action_buy"
            is HomeActions.DownloadImage -> "${FLAVOR}_download_image"
            is HomeActions.ShowBuyDialog -> "${FLAVOR}_dialog_show"
            is HomeActions.DownloadImageByBonifiedAd -> "${FLAVOR}_download_image_by_bonified_ad"
            is HomeActions.Info -> "${FLAVOR}_action_info"
            is HomeActions.Like -> "${FLAVOR}_action_like"
            is HomeActions.New -> "${FLAVOR}_action_new_quote"
            is HomeActions.Owner -> "${FLAVOR}_action_owner"
            is HomeActions.ReConnection -> "${FLAVOR}_action_reconnection"
            is HomeActions.Send -> "${FLAVOR}_action_share"
            is HomeActions.ShowImage -> "${FLAVOR}_action_image"
            is HomeActions.ShowNoConnectionDialog -> "${FLAVOR}_action_without_connection"
            is HomeActions.ShowToastDownload -> "${FLAVOR}_action_toast_download"
            is HomeActions.SureDownloadImageAgain -> "${FLAVOR}_action_download_image_again"
            is HomeActions.CloseDialogDownLoadImageAgain -> "${FLAVOR}_action_cancel_download_image_again"
        }
    }
}

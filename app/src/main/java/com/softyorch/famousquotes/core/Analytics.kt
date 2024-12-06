package com.softyorch.famousquotes.core

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.mainActivity.MainActivity.Companion.firebaseAnalytics
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.utils.LevelLog.DEBUG
import com.softyorch.famousquotes.utils.writeLog

sealed class Analytics(val name: String) {
    data class Action(val id: HomeActions) : Analytics(actionName(id))
    data class Banner(val id: String = "ad_banner") : Analytics(id)
    data class Interstitial(val id: String = "ad_interstitial") : Analytics(id)

    companion object {
        fun sendAction(action: Analytics) {
            if (!isRunningUnitTest()) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, FLAVOR)
                    param(FirebaseAnalytics.Param.ITEM_NAME, action.name)
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "clicked")
                }

                writeLog(
                    level = DEBUG, text = "[Analytics] -> send action from: ${
                        MainActivity.packageAppName
                    } to Analytics: ${action.name}"
                )
            }
        }

        private const val FLAVOR = BuildConfig.FLAVOR

        private fun actionName(action: HomeActions) = when (action) {
            is HomeActions.HideLoading -> "${FLAVOR}_loading_off"
            is HomeActions.DownloadImage -> "${FLAVOR}_download_image"
            is HomeActions.DownloadImageByBonifiedAd -> "${FLAVOR}_download_image_by_bonified_ad"
            is HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd -> "${FLAVOR}_cancel_or_error_download_by_bonified_ad"
            is HomeActions.Info -> "${FLAVOR}_action_info"
            is HomeActions.Like -> "${FLAVOR}_action_like"
            is HomeActions.New -> "${FLAVOR}_action_new_quote"
            is HomeActions.Owner -> "${FLAVOR}_action_owner"
            is HomeActions.ReConnection -> "${FLAVOR}_action_reconnection"
            is HomeActions.ShareWithImage -> "${FLAVOR}_action_share_with_image"
            is HomeActions.ShareText -> "${FLAVOR}_action_share_text"
            is HomeActions.ShowImage -> "${FLAVOR}_action_image"
            is HomeActions.ShowNoConnectionDialog -> "${FLAVOR}_action_without_connection"
            is HomeActions.ShowToastDownload -> "${FLAVOR}_action_toast_download"
            is HomeActions.SureDownloadImageAgain -> "${FLAVOR}_action_download_image_again"
            is HomeActions.CloseDialogDownLoadImageAgain -> "${FLAVOR}_action_cancel_download_image_again"
            is HomeActions.QuoteShown -> "${FLAVOR}_action_set_shown_image"
        }

        private fun isRunningUnitTest(): Boolean {
            return try {
                Class.forName("org.junit.runner.RunWith")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }
}

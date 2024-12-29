package com.softyorch.famousquotes.core

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.ui.mainActivity.MainActivity.Companion.firebaseAnalytics
import com.softyorch.famousquotes.ui.screens.detail.DetailActions
import com.softyorch.famousquotes.ui.screens.home.HomeActions
import com.softyorch.famousquotes.ui.screens.settings.SettingsActions
import com.softyorch.famousquotes.utils.LevelLog.DEBUG
import com.softyorch.famousquotes.utils.writeLog

sealed class Analytics(val name: String) {
    data class Action(val id: HomeActions) : Analytics(actionName(id))
    data class ActionDetails(val id: DetailActions) : Analytics(actionDetailsName(id))
    data class ActionSettings(val id: SettingsActions) : Analytics(actionSettingsName(id))
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
            is HomeActions.Info -> "${FLAVOR}_action_info"
            is HomeActions.NewQuoteRequest -> "${FLAVOR}_action_new_quote_request"
            is HomeActions.NewQuote -> "${FLAVOR}_action_new_quote"
            is HomeActions.ShareWithImage -> "${FLAVOR}_action_share_with_image"
            is HomeActions.ShareText -> "${FLAVOR}_action_share_text"
            is HomeActions.Owner -> "${FLAVOR}_action_owner"
            is HomeActions.Like -> "${FLAVOR}_action_like"
            is HomeActions.Favorite -> "${FLAVOR}_action_favorite"
            is HomeActions.ShowImage -> "${FLAVOR}_action_image"
            is HomeActions.ShowNoConnectionDialog -> "${FLAVOR}_action_without_connection"
            is HomeActions.ReConnection -> "${FLAVOR}_action_reconnection"
            is HomeActions.ImageDownloadRequest -> "${FLAVOR}_image_download_request"
            is HomeActions.DownloadImage -> "${FLAVOR}_download_image"
            is HomeActions.ShowedOrCloseOrDismissedOrErrorDownloadByBonifiedAd -> "${FLAVOR}_cancel_or_error_download_by_bonified_ad"
            is HomeActions.ShowToastDownload -> "${FLAVOR}_action_toast_download"
            is HomeActions.QuoteShown -> "${FLAVOR}_action_set_shown_image"
        }

        private fun actionDetailsName(action: DetailActions) = when (action) {
            is DetailActions.LoadQuoteData -> "${FLAVOR}_action_load_quote_data"
            is DetailActions.SetLikeQuote -> "${FLAVOR}_action_set_like_quote"
            is DetailActions.SetFavoriteQuote -> "${FLAVOR}_action_set_favorite_quote"
            is DetailActions.DownloadQuote -> "${FLAVOR}_action_download_quote"
            is DetailActions.HowToShareQuote -> "${FLAVOR}_action_how_to_share_quote"
            is DetailActions.ShareQuoteAs -> "${FLAVOR}_action_share_quote_as_${action.shareAs}"
            is DetailActions.OwnerQuoteIntent -> "${FLAVOR}_action_owner_quote_intent"
            is DetailActions.ShowNoConnectionDialog -> "${FLAVOR}_action_without_connection_dialog"
            is DetailActions.HideControls -> "${FLAVOR}_action_hide_controls"
            is DetailActions.ExitDetail -> "${FLAVOR}_action_exit_detail"
        }

        private fun actionSettingsName(action: SettingsActions) = when (action) {
            is SettingsActions.GetSettings -> "${FLAVOR}_action_get_settings"
            is SettingsActions.AutoDarkMode -> "${FLAVOR}_action_auto_dark_mode_${action.autoDarkMode}"
            is SettingsActions.DarkMode -> "${FLAVOR}_action_dark_mode_${action.darkMode}"
            is SettingsActions.LeftHanded -> "${FLAVOR}_action_left_handed_${action.leftHanded}"
            is SettingsActions.NotificationChannel -> "${FLAVOR}_action_notification_channel_${action.notificationChannel}"
            is SettingsActions.IsShowOnBoarding -> "${FLAVOR}_action_is_show_on_boarding"
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

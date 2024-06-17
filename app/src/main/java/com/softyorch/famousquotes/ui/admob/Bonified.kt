package com.softyorch.famousquotes.ui.admob

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Bonified @Inject constructor() {

    companion object {
        lateinit var instance: Bonified
    }

    private var rewardedAd: RewardedAd? = null
    private lateinit var adRequest: AdRequest

    init {
        instance = this
        getAdRequest()
    }

    @Composable
    fun Show(
        isVisible: Boolean,
        onAction: (BonifiedAdState) -> Unit
    ) {
        val context = LocalContext.current

        if (!::adRequest.isInitialized) getAdRequest()

        loadBonified { onAction(it) }

        try {
            if (isVisible) {
                onAction(BonifiedAdState.Loading)

                val adId = BuildConfig.ID_BONIFIED_DOWNLOAD_IMAGE

                RewardedAd.load(
                    context,
                    adId,
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            writeLog(LevelLog.ERROR, text = "[BonifiedAd] -> on failure load: ${adError.message}")
                            rewardedAd = null
                        }

                        override fun onAdLoaded(ad: RewardedAd) {
                            writeLog(text = "[BonifiedAd] -> on ad loaded: $ad")
                            rewardedAd = ad
                            rewardedAd?.let { adR ->
                                adR.show(MainActivity.instance) { rewardItem ->
                                    writeLog(text = "[BonifiedAd] -> onUserEarnedReward: $rewardItem")
                                    onAction(BonifiedAdState.Reward)
                                }
                            } ?: run {
                                writeLog(level = LevelLog.ERROR, text = "[BonifiedAd] -> The rewarded ad wasn't ready yet.")
                            }
                        }
                    }
                )
            }
        } catch (ex: Exception) {
            writeLog(level = LevelLog.ERROR, text = "[BonifiedAd] -> Show Bonified error: ${ex.message}")
            onAction(BonifiedAdState.Error)
        }
    }

    private fun loadBonified(onAction: (BonifiedAdState) -> Unit) {
        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                writeLog(level = LevelLog.ERROR, text = "[BonifiedAd] -> onAdDismissedFullScreenContent")
                onAction(BonifiedAdState.OnDismissed)
                rewardedAd = null
            }

            override fun onAdClicked() {
                writeLog(text = "[BonifiedAd] -> onAdClicked")
                onAction(BonifiedAdState.Clicked)
            }

            override fun onAdShowedFullScreenContent() {
                writeLog(text = "[BonifiedAd] -> onAdShowedFullScreenContent")
                onAction(BonifiedAdState.Showed)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                writeLog(level = LevelLog.ERROR, text = "[BonifiedAd] -> onAdFailedToShowFullScreenContent: ${adError.message}")
                onAction(BonifiedAdState.Error)
                rewardedAd = null
            }

            override fun onAdImpression() {
                writeLog(text = "[BonifiedAd] -> onAdImpression")
                onAction(BonifiedAdState.Impression)
            }
        }
    }

    private fun getAdRequest() {
        adRequest = AdRequest.Builder().build()
    }
}
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
        val adId = BuildConfig.ID_BONIFIED_DOWNLOAD_IMAGE

        if (!::adRequest.isInitialized) getAdRequest()

        loadBonified { onAction(it) }

        if (isVisible) {
            onAction(BonifiedAdState.Loading)
            rewardedAd?.let { ad ->
                ad.show(MainActivity.instance) { _ -> onAction(BonifiedAdState.Reward) }
            } ?: run {
                writeLog(level = LevelLog.ERROR, text = "The rewarded ad wasn't ready yet.")
            }
        }

        RewardedAd.load(
            context,
            adId,
            adRequest,
            object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                writeLog(LevelLog.ERROR, text = adError.message)
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                writeLog(LevelLog.ERROR, text = ad.toString())
                rewardedAd = ad
            }
        })
    }

    private fun loadBonified(onAction: (BonifiedAdState) -> Unit) {
        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onAction(BonifiedAdState.Close)
                rewardedAd = null
            }

            override fun onAdClicked() {
                onAction(BonifiedAdState.Clicked)
            }

            override fun onAdShowedFullScreenContent() {
                onAction(BonifiedAdState.Showed)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onAction(BonifiedAdState.Error)
                rewardedAd = null
            }

            override fun onAdImpression() {
                onAction(BonifiedAdState.Impression)
            }
        }
    }

    private fun getAdRequest() {
        adRequest = AdRequest.Builder().build()
    }
}
package com.softyorch.famousquotes.ui.admob

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Singleton

@Singleton
class Interstitial {

    companion object {
        lateinit var instance: Interstitial
    }

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest

    init {
        instance = this
        getAdRequest()
    }

    @Composable
    fun Show(
        isVisible: Boolean,
        onAction: (InterstitialAdState) -> Unit,
    ) {
        val context = LocalContext.current

        if (!::adRequest.isInitialized) getAdRequest()

        loadInter { onAction(it) }

        try {
            if (isVisible) {
                onAction(InterstitialAdState.Loading)

                val adId = BuildConfig.ID_INTERSTITIAL_OTHER_QUOTE

                InterstitialAd.load(
                    context,
                    adId,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            writeLog(ERROR, "[Interstitial] -> Error Admob: ${error.message}")
                            mInterstitialAd = null
                            onAction(InterstitialAdState.Error(error.message))
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            writeLog(text = "[Interstitial] -> On ad loaded: $ad")
                            mInterstitialAd = ad
                            mInterstitialAd?.let { adItem ->
                                adItem.show(MainActivity.instance)
                                writeLog(text = "[Interstitial] -> On ad showed: $adItem")
                                onAction(InterstitialAdState.Showed)
                            } ?: run {
                                writeLog(
                                    text = "[Interstitial] -> The rewarded ad wasn't ready yet.",
                                    throwable = Throwable("[Interstitial] Interstitial Throw")
                                )
                            }
                        }
                    }
                )
            }
        } catch (ex: Exception) {
            writeLog(level = ERROR, text = "Error Show Interstitial ${ex.message}", ex)
            onAction(InterstitialAdState.Error(ex.message ?: ""))
        }
    }

    private fun loadInter(onAction: (InterstitialAdState) -> Unit) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onAction(InterstitialAdState.Close)
                getAdRequest()
            }

            override fun onAdClicked() {
                onAction(InterstitialAdState.Clicked)
                Analytics.sendAction(Analytics.Interstitial())
            }

            override fun onAdShowedFullScreenContent() {
                onAction(InterstitialAdState.Showed)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onAction(InterstitialAdState.Error(adError.message))
                getAdRequest()
            }

            override fun onAdImpression() {
                onAction(InterstitialAdState.Impression)
            }
        }
    }


    private fun getAdRequest() {
        adRequest = AdRequest.Builder().build()
    }
}

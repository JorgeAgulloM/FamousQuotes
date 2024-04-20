package com.softyorch.famousquotes.ui.admob

import android.app.Activity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog

@Composable
fun Interstitial(
    isVisible: Boolean,
    onAction: (InterstitialAdState) -> Unit,
) {
    val context = LocalContext.current
    val adId = BuildConfig.ID_INTERSTITIAL_OTHER_QUOTE

    if (isVisible) LaunchedEffect(key1 = true) {
        onAction(InterstitialAdState.Loading)
        InterstitialAd.load(
            context,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    writeLog(LevelLog.ERROR, "[Interstitial] -> Error Admob")
                    onAction(InterstitialAdState.Error)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)

                    interstitialAd.show(context as Activity)
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                onAction(InterstitialAdState.Close)
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                onAction(InterstitialAdState.Clicked)
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                onAction(InterstitialAdState.Showed)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                                onAction(InterstitialAdState.Error)
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                onAction(InterstitialAdState.Impression)
                            }
                        }
                }
            }
        )
    }
}
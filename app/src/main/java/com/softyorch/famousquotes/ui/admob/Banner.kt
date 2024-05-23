package com.softyorch.famousquotes.ui.admob

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.FamousQuotesApp.Companion.adRequest
import javax.inject.Singleton

@Singleton
class Banner {

    companion object {
        val bannerInstance: Banner = Banner()
    }

    private var adView: AdView? = null
    @Composable
    fun Show() {
        if (adView == null) StartAdView()

        ElevatedCard(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 24.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.elevatedCardElevation(2.dp)
        ) {
            AndroidView(
                factory = { _ -> adView!! },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    fun StartAdView() {
        val context = LocalContext.current
        val currentWidth = LocalConfiguration.current.screenWidthDp

        adView = AdView(context).apply {
            setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context, currentWidth
                )
            )
            adUnitId = BuildConfig.ID_BANNER_HOME
            loadAd(adRequest)
        }
    }
}

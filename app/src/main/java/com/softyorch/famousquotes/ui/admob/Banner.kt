package com.softyorch.famousquotes.ui.admob

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.FamousQuotesApp.Companion.adRequest
import com.softyorch.famousquotes.core.Analytics
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import javax.inject.Singleton

@Singleton
class Banner {

    companion object {
        val bannerInstance: Banner = Banner()
        var heightBanner: Int = 96
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
        var adLoadedState by remember { mutableStateOf(false) }

        if (adLoadedState) Show()

        adView = AdView(context).apply {
            setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context, currentWidth
                )
            )
            adUnitId = BuildConfig.ID_BANNER_HOME
            loadAd(adRequest)

            adListener = object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    Analytics.sendAction(Analytics.Banner())
                }

                override fun onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked()
                    Analytics.sendAction(Analytics.Banner())
                }

                override fun onAdLoaded() {
                    val density = resources.displayMetrics.density
                    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(MainActivity.instance, getAdWidth()) // ancho en dp
                    val heightInDp = adSize.height
                    val heightInPixels = (heightInDp * density).toInt()
                    heightBanner = pxToIntFoDpUse(heightInPixels)
                    adLoadedState = true
                }
            }
        }
    }

    fun getAdWidth(): Int {
        val displayMetrics = MainActivity.instance.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels // Ancho en píxeles
        val density = displayMetrics.density // Densidad de píxeles
        return (widthPixels / density).toInt() // Convertir a dp
    }

    fun pxToIntFoDpUse(px: Int): Int {
        val density = MainActivity.instance.resources.displayMetrics.density
        return ((px.toFloat() / density) + 32f).toInt()
    }
}

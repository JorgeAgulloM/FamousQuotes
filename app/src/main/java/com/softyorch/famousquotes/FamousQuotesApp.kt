package com.softyorch.famousquotes

import android.app.Application
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FamousQuotesApp : Application() {

    companion object {
        lateinit var adRequest: AdRequest
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        adRequest = AdRequest.Builder().build()
        RequestConfiguration.Builder().setTestDeviceIds(
            listOf(
                "593592E028848EA2BF3B8AEDC4D3D8EE",
                "5B0C38DF42C762016CCFB455D4473887",
                "9BAE37CC1123C50AD21A352FC5573000",
                "33A364FD67DD37E662A41C4D49D7A996"
            )
        )
    }
}

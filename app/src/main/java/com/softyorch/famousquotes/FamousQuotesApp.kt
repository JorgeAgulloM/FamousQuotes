package com.softyorch.famousquotes

import android.app.Application
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FamousQuotesApp: Application() {

    companion object {
        lateinit var adRequest: AdRequest
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this){}
        adRequest = AdRequest.Builder().build()
    }
}

package com.softyorch.famousquotes.utils

import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.softyorch.famousquotes.ui.mainActivity.MainActivity
import java.util.concurrent.atomic.AtomicBoolean

class RequestGrantedProtectionData(private val mainActivity: MainActivity) {

    private lateinit var consentInformation: ConsentInformation
    private var isMobileAdsInitializedCalled = AtomicBoolean(false)
    private val consent = "Consent"
    private val notConsent = "Not consent"

    fun getConsent() {
        if (isMobileAdsInitializedCalled.get()) {
            writeLog(LevelLog.INFO, "[RequestGrantedProtectionData] -> $consent")
            return
        }

        val params: ConsentRequestParameters = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(mainActivity)
        consentInformation.requestConsentInfoUpdate(
            mainActivity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(mainActivity) {
                    if (consentInformation.canRequestAds()) {
                        isMobileAdsInitializedCalled.set(true)
                        writeLog(LevelLog.INFO, "[RequestGrantedProtectionData] -> $consent")
                    } else {
                        isMobileAdsInitializedCalled.set(false)
                        writeLog(LevelLog.INFO, "[RequestGrantedProtectionData] -> $notConsent")
                    }
                }
            },
            {
                writeLog(
                    LevelLog.INFO,
                    "[RequestGrantedProtectionData] -> Error: ${it.errorCode} - ${it.message}"
                )
            }
        )
    }
}

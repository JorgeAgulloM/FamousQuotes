package com.softyorch.famousquotes.core

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.softyorch.famousquotes.BuildConfig

class Crashlytics {
    companion object {
        private const val FLAVOR = BuildConfig.FLAVOR
        fun sendError(error: String, throwable: Throwable) {
            // Add context error
            Firebase.crashlytics.setCustomKey("$FLAVOR Error", error)
            Firebase.crashlytics.log("Error occurred in someFunction")
            // Send Error
            Firebase.crashlytics.recordException(throwable)
        }
    }
}

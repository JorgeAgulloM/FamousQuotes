package com.softyorch.famousquotes.core

import com.softyorch.famousquotes.BuildConfig

const val FIREBASE_NOTIFICATION_CHANNEL_1 = "famous_quotes_channel_1"
const val URL_STORAGE_PROJECT = "/images/famous_quotes/${BuildConfig.DB_COLLECTION}/"
val APP_NAME = BuildConfig.APP_TITLE.split("_")[2]
const val FIREBASE_TIMEOUT: Long = 10000L
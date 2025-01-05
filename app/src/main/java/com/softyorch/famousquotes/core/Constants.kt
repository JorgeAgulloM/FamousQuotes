package com.softyorch.famousquotes.core

import com.softyorch.famousquotes.BuildConfig

const val FIREBASE_NOTIFICATION_CHANNEL_1 = "famous_quotes_channel_1"
const val URL_STORAGE_PROJECT = "/images/famous_quotes/${BuildConfig.DB_COLLECTION}/"
val APP_NAME = BuildConfig.APP_TITLE.split("_")[2]
const val FIREBASE_TIMEOUT: Long = 10000L
const val PAY_ME_ONE_COFFEE_URI: String = "https://www.paypal.com/qrcodes/managed/3e195be5-6b42-45f8-a0fc-210d4b267689?utm_source=consapp_download&amount=1.0&currency_code=EUR"
const val BUY_ME_A_COFFEE_URI: String = "https://www.buymeacoffee.com/SrYorch"
const val EMAIL_SOFT_YORCH: String = "softyorch@gmail.com"
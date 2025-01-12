package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString
import com.softyorch.famousquotes.utils.getResourceDrawableIdentifier
import com.softyorch.famousquotes.utils.userId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class Intents @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun goToSearchOwnerInBrowser(owner: String) {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_WEB_SEARCH
            ).apply {
                putExtra("query", owner)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    suspend fun goToSupportEmail() {
        coroutineScope {
            val id = context.userId()
            val appNameValue = context.getResourceString(BuildConfig.APP_TITLE)
            val appName = if (appNameValue.contains("_"))
                context.getResourceDrawableIdentifier(appNameValue)
            else appNameValue
            val subject = "$appName - Please, I need support"
            val bodyText = "$appName - App \nUser id: $id\n\n"

            val intentSelector = intentSelector(subject, bodyText)

            context.startActivity(intentSelector)
        }
    }

    suspend fun goToBuyMeACoffee() {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(BUY_ME_A_COFFEE_URI)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    suspend fun goToCoffeeWithPayPal() {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(PAY_ME_ONE_COFFEE_URI)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    suspend fun goToGooglePlay() {
        coroutineScope {
            val intent = Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=${
                        appNameInDebug(context.packageName)
                    }"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    private fun intentSelector(subject: String, bodyText: String): Intent {
        val intentSendTo = Intent(
            Intent.ACTION_SENDTO
        ).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_SOFT_YORCH))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val intent = Intent.createChooser(
            Intent(
                Intent.ACTION_SEND
            ).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_SOFT_YORCH))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, "selecciona tu cliente de e-Mail..."
        )

        return if (intentSendTo.resolveActivity(context.packageManager) != null) {
            intentSendTo
        } else intent
    }

    private fun appNameInDebug(name: String) =
        if (BuildConfig.DEBUG) name.removeSuffix(".dev") else name
}

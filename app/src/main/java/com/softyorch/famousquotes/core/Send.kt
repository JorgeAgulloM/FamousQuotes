package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Send @Inject constructor(@ApplicationContext private val context: Context) {

    private val name = context.getResourceString(BuildConfig.APP_TITLE)
    private val pkgName = BuildConfig.DB_COLLECTION.replace("_quotes", "")

    fun sendDataTo(data: String) {
        val htmlFormattedMessage = "https://play.google.com/store/apps/details?id=com.softyorch.famousquotes.$pkgName"
        val sendText = "$data\n\n$name\n\n$htmlFormattedMessage"

        context.startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, sendText)
                }, "Today quote"
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

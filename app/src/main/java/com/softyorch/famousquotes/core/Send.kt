package com.softyorch.famousquotes.core

import android.content.Context
import android.content.Intent
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.ui.utils.extFunc.getResourceStringNotComposable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Send @Inject constructor(@ApplicationContext private val context: Context) {

    private val name = context.getResourceStringNotComposable(BuildConfig.APP_TITLE)

    fun sendDataTo(data: String) {
        val sendFrom = "Compartir desde"
        val sendText = "$sendFrom $name\n\n$data"
        context.startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, sendText)
                }, "Frase del día"
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

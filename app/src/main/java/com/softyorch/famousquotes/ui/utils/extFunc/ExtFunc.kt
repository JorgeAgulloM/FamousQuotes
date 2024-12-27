package com.softyorch.famousquotes.ui.utils.extFunc

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable

@SuppressLint("DiscouragedApi")
@Composable
fun Context.getResourceDrawableIdentifier(name: String): Int? {
    if (name.isBlank()) return null

    return this.resources.getIdentifier(name, "drawable", this.packageName)
}

@SuppressLint("DiscouragedApi")
fun Context.getResourceString(name: String): String {
    if (name.isBlank()) return name

    val intResource = this.resources.getIdentifier(name, "string", this.packageName)
    return getString(intResource)
}

fun Context.copyToClipboard(id: String, text: String) {
    val clipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(id, text)
    clipboardManager.setPrimaryClip(clip)
}

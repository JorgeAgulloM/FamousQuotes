package com.softyorch.famousquotes.ui.utils.extFunc

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@SuppressLint("DiscouragedApi")
@Composable
fun Context.getResourceStringComposable(name: String): String {
    if (name.isBlank()) return name

    val intResource = this.resources.getIdentifier(name, "string", this.packageName)
    return stringResource(intResource)
}

@SuppressLint("DiscouragedApi")
fun Context.getResourceStringNotComposable(name: String): String {
    if (name.isBlank()) return name

    val intResource = this.resources.getIdentifier(name, "string", this.packageName)
    return getString(intResource)
}

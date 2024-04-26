package com.softyorch.famousquotes.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast

fun writeLog(level: LevelLog = LevelLog.INFO, text: String) {
    when (level) {
        LevelLog.ERROR -> Log.e("LOGTAG", text)
        LevelLog.WARN -> Log.w("LOGTAG", text)
        LevelLog.INFO -> Log.i("LOGTAG", text)
        LevelLog.DEBUG -> Log.d("LOGTAG", text)
    }
}

enum class LevelLog { ERROR, WARN, INFO, DEBUG }

fun Context.showToast(msg: String, time: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    this, msg, time
).show()

inline fun <T> sdk33AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) onSdk() else null

inline fun <T> sdk26AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) onSdk() else null

@SuppressLint("DiscouragedApi")
fun Context.getResourceDrawableIdentifier(name: String): Int? {
    if (name.isBlank()) return null
    return this.resources.getIdentifier(name, "drawable", this.packageName)
}

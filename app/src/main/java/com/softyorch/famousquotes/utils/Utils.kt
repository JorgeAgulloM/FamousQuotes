package com.softyorch.famousquotes.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R

fun writeLog(level: LevelLog = LevelLog.INFO, text: String) {
    val isTest = !IsTestMode.isTest
    when (level) {
        LevelLog.ERROR -> if (isTest) Log.e("LOGTAG", text)
        LevelLog.WARN -> if (isTest) Log.w("LOGTAG", text)
        LevelLog.INFO -> if (isTest) Log.i("LOGTAG", text)
        LevelLog.DEBUG -> if (isTest) Log.d("LOGTAG", text)
    }
}

object IsTestMode {
    var isTest: Boolean = false
}

enum class LevelLog { ERROR, WARN, INFO, DEBUG }

fun Context.showToast(msg: String, time: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    this, msg, time
).show()

inline fun <T> sdk33AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) onSdk() else null

inline fun <T> sdk31AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) onSdk() else null

val isSdk31OrUp = sdk31AndUp { true } ?: false

inline fun <T> sdk26AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) onSdk() else null

@SuppressLint("DiscouragedApi")
fun Context.getResourceDrawableIdentifier(name: String): Int? {
    if (name.isBlank()) return null
    return this.resources.getIdentifier(name, "drawable", this.packageName)
}

@Composable
fun appIcon() = painterResource(
    when (BuildConfig.ICON) {
        "historical_icon" -> R.drawable.historical_icon
        "uplifting_icon" -> R.drawable.uplifting_icon
        "biblical_icon" -> R.drawable.biblical_icon
        else -> R.drawable.default_icon
    }
)

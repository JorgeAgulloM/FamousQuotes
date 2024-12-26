package com.softyorch.famousquotes.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.core.APP_NAME
import com.softyorch.famousquotes.core.Crashlytics
import java.io.File

fun writeLog(level: LevelLog = LevelLog.INFO, text: String, throwable: Throwable? = null) {
    val isTest = !IsTestMode.isTest
    LogForDebug(level, isTest, text)
    LogForProduction(level, text, throwable)
}

private fun LogForDebug(
    level: LevelLog,
    isTest: Boolean,
    text: String
) {
    if (BuildConfig.DEBUG) when (level) {
        LevelLog.ERROR -> if (isTest) Log.e("LOGTAG", text)
        LevelLog.WARN -> if (isTest) Log.w("LOGTAG", text)
        LevelLog.INFO -> if (isTest) Log.i("LOGTAG", text)
        LevelLog.DEBUG -> if (isTest) Log.d("LOGTAG", text)
    }
}

private fun LogForProduction(
    level: LevelLog,
    text: String,
    throwable: Throwable?
) {
    if (!BuildConfig.DEBUG) when (level) {
        LevelLog.ERROR -> {
            Crashlytics.sendError(text, throwable!!)
            Log.e("LOGTAG", text)
        }

        else -> Unit
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

inline fun <T> sdk32AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) onSdk() else null

inline fun <T> sdk30AndUp(onSdk: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) onSdk() else null

val sdk29AndDown = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

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

fun doesDownloadPathFileExist(fileName: String): Boolean {
    val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    val completePath = "$path/$APP_NAME-$fileName.png"

    return File(completePath).exists()
}

@Suppress("DEPRECATION")
fun isFullScreenMode(activity: ComponentActivity): Boolean =
    sdk30AndUp {
        val insets = activity.window.decorView.rootWindowInsets
        val isVisible = insets?.isVisible(android.view.WindowInsets.Type.systemBars()) ?: true
        !isVisible
    } ?: run {
        val flags = activity.window.decorView.systemUiVisibility
        (flags and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0
    }

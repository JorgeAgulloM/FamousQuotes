package com.softyorch.famousquotes.utils

import android.util.Log

fun writeLog(level: LevelLog, text: String) {
    when (level) {
        LevelLog.ERROR -> Log.e("LOGTAG", text)
        LevelLog.WARN -> Log.w("LOGTAG", text)
        LevelLog.INFO -> Log.i("LOGTAG", text)
        LevelLog.DEBUG -> Log.d("LOGTAG", text)
    }
}

enum class LevelLog { ERROR, WARN, INFO, DEBUG }

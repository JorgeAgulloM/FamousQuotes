package com.softyorch.famousquotes.domain.utils

import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import java.util.Calendar
import java.util.TimeZone

fun getTodayId(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val oneHourInMillis = 1000 * 60 * 60
    return (calendar.timeInMillis - oneHourInMillis).toString().also {
        writeLog(LevelLog.INFO, "Id generated: $it")
    }
}
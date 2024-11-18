package com.softyorch.famousquotes.domain.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.random.Random

fun getTodayId(): String {
   return getCalendarZero().toString()
}

fun generateRandomId(): String {
    val calendar = Calendar.getInstance()
    val dayOfYearRandom = Random.nextInt(1, 366)
    calendar.timeInMillis = getCalendarZero()
    calendar.set(Calendar.DAY_OF_YEAR, dayOfYearRandom)
    return calendar.timeInMillis.toString()
}

private fun getCalendarZero(): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val oneHourInMillis = 1000 * 60 * 60
    return (calendar.timeInMillis - oneHourInMillis)
}

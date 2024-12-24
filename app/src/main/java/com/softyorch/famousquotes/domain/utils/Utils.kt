package com.softyorch.famousquotes.domain.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.random.Random

fun getTodayId(): String {
   return getCalendarZero().toString()
}

fun generateRandomId(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val dayOfYearRandom = Random.nextInt(135, 365)
    calendar.timeInMillis = getCalendarZero()
    calendar[Calendar.DAY_OF_YEAR] = dayOfYearRandom
    return calendar.timeInMillis.toString()
}

private fun getCalendarZero(): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar[Calendar.YEAR] = 2024
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    val oneHourInMillis = 1000 * 60 * 60
    return (calendar.timeInMillis - oneHourInMillis)
}
///images/famous_quotes/uplifting_quotes/1715641200000
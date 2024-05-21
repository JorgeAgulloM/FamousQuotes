package com.softyorch.famousquotes.domain.utils

import java.util.Calendar
import java.util.TimeZone

fun getTodayId(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val oneHourInMillis = 1000 * 60 * 60
    return (calendar.timeInMillis - oneHourInMillis).toString()
}

fun versionList(nameVersion: String): List<Int> =
    nameVersion.split(".").map { it.toInt() }

fun emptyVersionList(): List<Int> = listOf(0, 0, 0)

fun versionToString(versionList: List<Int>): String =
    "${versionList[0]}.${versionList[1]}.${versionList[2]}"

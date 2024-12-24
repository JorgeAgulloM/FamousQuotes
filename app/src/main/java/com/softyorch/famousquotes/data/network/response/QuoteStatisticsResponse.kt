package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class QuoteStatisticsResponse(
    val likes: Int = 0,
    val showns: Int = 0,
    val favorites: Int = 0
)

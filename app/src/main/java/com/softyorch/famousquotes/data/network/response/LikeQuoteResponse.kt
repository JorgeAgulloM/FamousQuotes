package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class LikeQuoteResponse(
    val id: String = "",
    val likes: Int = 0,
    val like: Boolean = false
)

package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class UserShownResponse(
    val shownQuotes: List<String>? = mutableListOf()
)

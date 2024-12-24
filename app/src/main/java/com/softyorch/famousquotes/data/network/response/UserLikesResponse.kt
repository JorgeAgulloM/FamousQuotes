package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class UserLikesResponse(
    val likeQuotes: List<String>? = mutableListOf()
)

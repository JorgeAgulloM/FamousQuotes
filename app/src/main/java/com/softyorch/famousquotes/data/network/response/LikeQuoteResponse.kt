package com.softyorch.famousquotes.data.network.response

data class LikeQuoteResponse(
    val id: String = "",
    val likes: Int = 0,
    val like: Boolean = false
)

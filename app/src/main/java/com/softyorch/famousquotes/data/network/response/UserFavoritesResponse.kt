package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class UserFavoritesResponse(
    val favoriteQuotes: List<String>? = mutableListOf()
)

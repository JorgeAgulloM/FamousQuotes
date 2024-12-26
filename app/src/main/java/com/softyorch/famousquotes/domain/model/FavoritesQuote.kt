package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.FavoritesQuoteResponse

data class FavoritesQuote(
    val favorites: Int = 0
) {
    companion object {
        fun FavoritesQuoteResponse.toDomain(): FavoritesQuote = FavoritesQuote(favorites)
    }
}

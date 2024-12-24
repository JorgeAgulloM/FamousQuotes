package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.LikesQuoteResponse

data class LikesQuote(
    val likes: Int
) {
    companion object {
        fun LikesQuoteResponse.toDomain(): LikesQuote = LikesQuote(this.likes)
    }
}

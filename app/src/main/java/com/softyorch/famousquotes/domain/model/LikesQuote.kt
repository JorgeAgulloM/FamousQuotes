package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse

data class LikesQuote(
    val likes: Int,
    val isLike: Boolean
) {
    companion object {
        fun LikeQuoteResponse.toDomain(): LikesQuote = LikesQuote(this.likes, like)
    }
}

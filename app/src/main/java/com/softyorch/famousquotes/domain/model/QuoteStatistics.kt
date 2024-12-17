package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.QuoteStatisticsResponse

data class QuoteStatistics(
    val likes: Int = 0,
    val showns: Int = 0,
    val favorites: Int = 0
) {
    companion object {
        fun QuoteStatisticsResponse.toQuoteStatistics(): QuoteStatistics = QuoteStatistics(
            likes = likes,
            showns = showns,
            favorites = favorites
        )
    }
}

package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.ShownQuoteResponse

data class ShownQuote(
    val shown: Int = 0
) {
    companion object {
        fun ShownQuoteResponse.toDomain(): ShownQuote = ShownQuote(showns)
    }
}

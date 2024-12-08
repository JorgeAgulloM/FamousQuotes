package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.response.QuoteResponse

interface IDatabaseQuoteService {
    suspend fun getQuote(id: String): QuoteResponse?
    suspend fun getRandomQuote(): QuoteResponse?
    suspend fun getAllQuotes(): List<QuoteResponse?>?
}

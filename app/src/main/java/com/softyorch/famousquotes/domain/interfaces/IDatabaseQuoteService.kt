package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.response.QuoteResponse
import kotlinx.coroutines.flow.Flow

interface IDatabaseQuoteService {
    suspend fun getQuoteFlow(id: String? = null): Flow<QuoteResponse?>
    suspend fun getRandomQuote(): Flow<QuoteResponse?>
    suspend fun getAllQuotes(): List<QuoteResponse?>?
}

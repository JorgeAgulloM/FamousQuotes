package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.response.QuoteResponse

interface IDatabaseService {
    suspend fun getQuote(id: String): QuoteResponse?
    suspend fun getRandomQuote(): QuoteResponse?
}

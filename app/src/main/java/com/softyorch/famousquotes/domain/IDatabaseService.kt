package com.softyorch.famousquotes.domain

import com.softyorch.famousquotes.data.network.response.QuoteResponse

interface IDatabaseService {

    suspend fun getQuote(id: String): QuoteResponse?
}

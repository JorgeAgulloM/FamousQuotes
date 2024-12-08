package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikesQuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import kotlinx.coroutines.flow.Flow

interface IDatabaseService {
    suspend fun getQuote(id: String): QuoteResponse?
    suspend fun getRandomQuote(): QuoteResponse?
    suspend fun setQuoteLikes(updateLikes: LikesDataDTO)
    suspend fun setQuoteShown(id: String)
    suspend fun setQuoteFavorite(updateFavorites: FavoritesDataDTO)
    suspend fun getLikesQuoteFlow(id: String): Flow<LikesQuoteResponse?>
    suspend fun getUserLikeQuote(id: String): Flow<Boolean?>
    suspend fun getAllQuotes(): List<QuoteResponse?>?
    suspend fun getLikeQuotes(): List<QuoteResponse?>?
    suspend fun getQuotesShown(): List<QuoteResponse?>?
}

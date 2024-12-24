package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteStatisticsResponse
import kotlinx.coroutines.flow.Flow

interface IDatabaseListService {
    suspend fun setQuoteLike(updateLikes: LikesDataDTO)
    suspend fun setQuoteShown(id: String)
    suspend fun setQuoteFavorite(updateFavorites: FavoritesDataDTO)
    suspend fun getUserLikeQuote(id: String): Flow<Boolean?>
    suspend fun getUserFavoriteQuote(id: String): Flow<Boolean?>
    suspend fun getQuoteStatisticsFlow(id: String): Flow<QuoteStatisticsResponse?>
    suspend fun getLikeQuotes(): List<QuoteResponse?>?
    suspend fun getShownQuotes(): List<QuoteResponse?>?
    suspend fun getFavoriteQuotes(): List<QuoteResponse?>?
}

package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.FavoritesQuoteResponse
import com.softyorch.famousquotes.data.network.response.LikesQuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.ShownQuoteResponse
import kotlinx.coroutines.flow.Flow

interface IDatabaseListService {
    suspend fun setQuoteLike(updateLikes: LikesDataDTO)
    suspend fun setQuoteShown(id: String)
    suspend fun setQuoteFavorite(updateFavorites: FavoritesDataDTO)
    suspend fun getQuoteLikesFlow(id: String): Flow<LikesQuoteResponse?>
    suspend fun getQuoteShownFlow(id: String): Flow<ShownQuoteResponse?>
    suspend fun getQuoteFavoritesFlow(id: String): Flow<FavoritesQuoteResponse?>
    suspend fun getUserLikeQuote(id: String): Flow<Boolean?>
    suspend fun getUserFavoriteQuote(id: String): Flow<Boolean?>
    suspend fun getLikeQuotes(): List<QuoteResponse?>?
    suspend fun getShownQuotes(): List<QuoteResponse?>?
    suspend fun getFavoriteQuotes(): List<QuoteResponse?>?
}
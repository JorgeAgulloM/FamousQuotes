package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import kotlinx.coroutines.flow.Flow

interface IDatabaseService {
    suspend fun getQuote(id: String): QuoteResponse?
    suspend fun getRandomQuote(): QuoteResponse?
    suspend fun likeDislikeQuote(updateLikes: LikesDataDTO)
    suspend fun getLikeQuoteFlow(id: String): Flow<LikeQuoteResponse?>
}

package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.model.LikesQuote
import com.softyorch.famousquotes.domain.model.LikesQuote.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetQuoteLikes @Inject constructor(private val dbService: IDatabaseService) {
    suspend operator fun invoke(): Flow<LikesQuote?> {
        val id = getTodayId()
        return dbService.getLikeQuoteFlow(id).map { it?.toDomain() }
    }
}

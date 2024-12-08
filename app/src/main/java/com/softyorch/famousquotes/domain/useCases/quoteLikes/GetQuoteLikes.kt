package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.LikesQuote
import com.softyorch.famousquotes.domain.model.LikesQuote.Companion.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetQuoteLikes @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(id: String): Flow<LikesQuote?> =
        dbService.getLikesQuoteFlow(id).map { it?.toDomain() }
}

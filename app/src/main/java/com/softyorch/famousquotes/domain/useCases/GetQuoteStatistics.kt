package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.QuoteStatistics
import com.softyorch.famousquotes.domain.model.QuoteStatistics.Companion.toQuoteStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetQuoteStatistics @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(id: String): Flow<QuoteStatistics?> =
        dbService.getQuoteStatisticsFlow(id).map { it?.toQuoteStatistics() }
}

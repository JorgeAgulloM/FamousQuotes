package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.FavoritesQuote
import com.softyorch.famousquotes.domain.model.FavoritesQuote.Companion.toDomain
import com.softyorch.famousquotes.domain.model.LikesQuote
import com.softyorch.famousquotes.domain.model.LikesQuote.Companion.toDomain
import com.softyorch.famousquotes.domain.model.PropertyStatistics
import com.softyorch.famousquotes.domain.model.ShownQuote
import com.softyorch.famousquotes.domain.model.ShownQuote.Companion.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStatistics @Inject constructor(private val dbService: IDatabaseListService) {

    suspend operator fun invoke(id: String): Flow<PropertyStatistics> = combine(
        getQuoteLikes(id),
        getQuoteShown(id),
        getQuoteFavorites(id)
    ) { likes, shown, favorites ->
        PropertyStatistics(
            likes = likes?.likes ?: 0,
            shown = shown?.shown ?: 0,
            favorites = favorites?.favorites ?: 0
        )
    }

    private suspend fun getQuoteLikes(id: String): Flow<LikesQuote?> =
        dbService.getQuoteLikesFlow(id).map { it?.toDomain() }

    private suspend fun getQuoteShown(id: String): Flow<ShownQuote?> =
        dbService.getQuoteShownFlow(id).map { it?.toDomain() }

    private suspend fun getQuoteFavorites(id: String): Flow<FavoritesQuote?> =
        dbService.getQuoteFavoritesFlow(id).map { it?.toDomain() }

}

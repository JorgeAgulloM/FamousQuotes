package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel.Companion.HTTP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// WIP
class GetAllQuotesFiltered @Inject constructor(
    private val dbService: IDatabaseService,
    private val storageService: IStorageService
) {
    suspend fun invoke(): Flow<List<FamousQuoteModel?>?> = flowOf(
        dbService.getFavoriteQuotes()?.map { quote ->
            quote?.let { quoteNotNull ->
                getImage(quoteNotNull.imageUrl).let { image ->
                    quoteNotNull.copy(imageUrl = image)
                }
            }?.toDomain()
        }?.filter {
            it?.imageUrl?.startsWith(HTTP) == true
        }
    )

    private suspend fun getImage(url: String): String = storageService.getImage(url) ?: url

}

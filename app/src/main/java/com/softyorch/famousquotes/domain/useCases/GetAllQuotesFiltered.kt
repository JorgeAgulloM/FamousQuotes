package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.core.FilterQuotes
import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.ui.screens.home.HomeViewModel.Companion.HTTP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// WIP
class GetAllQuotesFiltered @Inject constructor(
    private val dbService: IDatabaseListService,
    private val storageService: IStorageService
) {
    fun invoke(filter: FilterQuotes): Flow<List<FamousQuoteModel?>?> = flow {
        val getQuotes = getFunctionService(filter)

        val flow = getQuotes?.map { quote ->
            quote?.let { quoteNotNull ->
                getImage(quoteNotNull.imageUrl).let { image ->
                    quoteNotNull.copy(imageUrl = image)
                }
            }?.toDomain()
        }?.filter {
            it?.imageUrl?.startsWith(HTTP) == true
        }

        emit(flow)
    }

    private suspend fun getFunctionService(filter: FilterQuotes) =
        when (filter) {
            FilterQuotes.Likes -> dbService.getLikeQuotes()
            FilterQuotes.Shown -> dbService.getShownQuotes()
            FilterQuotes.Favorites -> dbService.getFavoriteQuotes()
        }

    private suspend fun getImage(url: String): String = storageService.getImage(url) ?: url

}

package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import javax.inject.Inject

class GetQuoteById @Inject constructor(
    private val dbService: IDatabaseQuoteService,
    private val storageService: IStorageService,
) {
    suspend operator fun invoke(id: String): FamousQuoteModel? {
        val quote = dbService.getQuote(id)?.toDomain()

        return quote?.imageUrl?.let {
            quote.copy(imageUrl = getImage(it))
        } ?: run { quote }
    }

    private suspend fun getImage(url: String): String =
        storageService.getImage(url) ?: url
}
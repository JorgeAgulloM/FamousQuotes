package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Inject

class GetTodayQuote @Inject constructor(
    private val dbService: IDatabaseQuoteService,
    private val storageService: IStorageService,
    private val defaultDatabase: IDefaultDatabase,
) {
    suspend operator fun invoke(id: String = getTodayId()): FamousQuoteModel {
        val quote = dbService.getQuote(id)?.toDomain()
            ?: quoteFromDefaultDb().also {
                writeLog(WARN, "[SelectRandomQuote] -> Quote has been getting from random!!")
            }
        val image = getImage(quote.imageUrl)

        return quote.copy(imageUrl = image)
    }

    suspend fun getRandomQuote(): FamousQuoteModel {
        val quote = dbService.getRandomQuote()?.toDomain()
        val image = getImage(url = quote?.imageUrl)

        return quote?.copy(imageUrl = image) ?: quoteFromDefaultDb()
    }

    private fun quoteFromDefaultDb() = defaultDatabase.getDefaultQuote().toDomain()

    private suspend fun getImage(url: String? = null): String {
        if (url != null) storageService.getImage(url).let {
            return it ?: defaultDatabase.getDefaultImage().also {
                writeLog(WARN, "[SelectRandomQuote] -> getImage(): Image from Default DB")
            }
        }

        return defaultDatabase.getDefaultImage().also {
            writeLog(WARN, "[SelectRandomQuote] -> getImage(): Image from Default DB")
        }
    }

}

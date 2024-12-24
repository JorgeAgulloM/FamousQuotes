package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTodayQuote @Inject constructor(
    private val dbService: IDatabaseQuoteService,
    private val storageService: IStorageService,
    private val defaultDatabase: IDefaultDatabase,
) {
    suspend operator fun invoke(id: String = getTodayId()): Flow<FamousQuoteModel?> =
        dbService.getQuoteFlow(id).mapFlow("getQuote")

    suspend fun getRandomQuote(): Flow<FamousQuoteModel?> =
        dbService.getRandomQuote().mapFlow("GetRandomQuote")

    private suspend fun Flow<QuoteResponse?>.mapFlow(callFrom: String): Flow<FamousQuoteModel> =
        this.map { quote: QuoteResponse? ->
            quote?.let {
                it.toDomain()

                val image = getImage(it.imageUrl)

                it.copy(imageUrl = image).toDomain()
            } ?: quoteFromDefaultDb().also {
                writeLog(WARN, "[SelectRandomQuote] -> Quote has been getting from $callFrom!!")
            }
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

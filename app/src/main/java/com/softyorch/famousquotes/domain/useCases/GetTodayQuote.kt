package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Inject

class GetTodayQuote @Inject constructor(
    private val dbService: IDatabaseService,
    private val storageService: IStorageService
) {
    suspend operator fun invoke(id: String = getTodayId()): FamousQuoteModel? {
        val quote = dbService.getQuote(id) ?: getRandomQuoteFromDb().also {
            writeLog(WARN, "[SelectRandomQuote] -> Quote has been getting from random!!")
        }
        val image = getImage(quote?.imageUrl)

        return quote?.toDomain()?.copy(imageUrl = image)
    }

    suspend fun getRandomQuote(): FamousQuoteModel? {
        val quote = getRandomQuoteFromDb()
        val image = getImage(url = quote?.imageUrl)

        return quote?.toDomain()?.copy(imageUrl = image)
    }

    private suspend fun getRandomQuoteFromDb() = dbService.getRandomQuote()

    private suspend fun getImage(url: String? = null): String {
        if (url != null) storageService.getImage(url).let {
            return it ?: getImageFromMemory().also {
                writeLog(WARN, "[SelectRandomQuote] -> getImage(): Image from Memory")
            }
        }

        return getImageFromMemory().also {
            writeLog(WARN, "[SelectRandomQuote] -> getImage(): Image from Memory")
        }
    }

    private fun getImageFromMemory(): String {
        val listImages = listOf(
            "bg_image_1",
            "bg_image_2",
            "bg_image_3",
            "bg_image_4",
            "bg_image_5",
            "bg_image_6",
            "bg_image_7",
            "bg_image_8",
            "bg_image_9",
            "bg_image_10",
        )

        return listImages.random()
    }

}

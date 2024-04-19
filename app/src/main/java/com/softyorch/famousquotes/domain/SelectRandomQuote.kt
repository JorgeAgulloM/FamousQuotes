package com.softyorch.famousquotes.domain

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import com.softyorch.famousquotes.domain.utils.getTodayId
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SelectRandomQuote @Inject constructor(
    private val dbService: IDatabaseService,
    private val storageService: IStorageService,
    private val datastore: IDatastore,
) {
    suspend operator fun invoke(): FamousQuoteModel? {
        val id = getTodayId()
        val image = getImage()

        val quote = dbService.getQuote(id) ?: dbService.getRandomQuote().also {
            writeLog(WARN, "[SelectRandomQuote] -> Quote has been getting from random!!")
        }

        return quote?.toDomain()?.copy(imageUrl = image)
    }

    private suspend fun getImage(): String {
        getImageFromDatastore().let { set ->
            set.toList().let { list ->
                if (list.isNotEmpty()) {
                    return list.random().also {
                        writeLog(INFO, "[SelectRandomQuote] -> getImage(): Image from datastore")
                    }
                } else {
                    getImageFromStorageServer()?.let {
                        return it.also {
                            writeLog(INFO, "[SelectRandomQuote] -> getImage(): Image from Storage")
                        }
                    }
                }
            }
        }

        return getImageFromMemory().also {
            writeLog(WARN, "[SelectRandomQuote] -> getImage(): Image from Memory")
        }
    }

    private suspend fun getImageFromDatastore() = datastore.getImageSet().first()

    private suspend fun getImageFromStorageServer(): String? {
        storageService.getImages().let {
            setImagesInDatastore(it)
            return it.randomOrNull()
        }
    }

    private fun getImageFromMemory(): String {
        val listImages = listOf(
            "bg_image1.webp",
            "bg_image2.webp",
            "bg_image3.webp",
            "bg_image4.webp",
            "bg_image5.webp",
            "bg_image6.webp",
            "bg_image7.webp",
            "bg_image8.webp",
            "bg_image9.webp",
            "bg_image10.webp",
        )

        return listImages.random()
    }

    private suspend fun setImagesInDatastore(images: List<String>) {
        datastore.setImageSet(images.toSet())
    }

}

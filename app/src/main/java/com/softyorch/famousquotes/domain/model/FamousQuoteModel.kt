package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.defaultDatabase.DefaultModel
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import java.util.Locale

data class FamousQuoteModel(
    val id: String,
    val owner: String,
    val body: String,
    val imageUrl: String,
    val likes: Int,
    val shown: Int,
    val favorites: Int
) {
    companion object {
        fun emptyModel(): FamousQuoteModel = FamousQuoteModel("", "", "", "", 0, 0, 0)

        fun QuoteResponse.toDomain(): FamousQuoteModel = FamousQuoteModel(
            id = id,
            owner = getDataLocal(owner),
            body = getDataLocal(quote),
            imageUrl = imageUrl,
            likes = likes,
            shown = showns,
            favorites = favorites
        )

        fun DefaultModel.toDomain(): FamousQuoteModel = FamousQuoteModel(
            id = id,
            owner = owner,
            body = getDataLocal(quote),
            imageUrl = imageUrl,
            likes = likes,
            shown = shown,
            favorites = favorites
        )

        private fun getDataLocal(quotes: List<String>): String = try {
            val local = Locale.getDefault().toString().split("_")[0].uppercase()
            quotes.first { it.startsWith(local) }.split("#")[1]
        } catch (ex: Exception) {
            quotes.first().split("#")[1]
        }
    }
}

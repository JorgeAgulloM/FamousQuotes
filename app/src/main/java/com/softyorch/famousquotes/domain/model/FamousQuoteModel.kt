package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.defaultDatabase.DefaultModel
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import java.util.Locale

data class FamousQuoteModel(
    val id: String,
    val owner: String,
    val body: String,
    val imageUrl: String,
) {
    companion object {
        fun emptyModel(): FamousQuoteModel = FamousQuoteModel("", "", "", "")

        fun QuoteResponse.toDomain(): FamousQuoteModel = FamousQuoteModel(
            id = id,
            owner = getDataLocal(owner),
            body = getDataLocal(quote),
            imageUrl = imageUrl
        )

        fun DefaultModel.toDomain(): FamousQuoteModel = FamousQuoteModel(
            id = id,
            owner = owner,
            body = getDataLocal(quote),
            imageUrl = imageUrl
        )

        private fun getDataLocal(quotes: List<String>): String = try {
            val local = Locale.getDefault().toString().split("_")[0].uppercase()
            quotes.first { it.startsWith(local) }.split("#")[1]
        } catch (ex: Exception) {
            quotes.first().split("#")[1]
        }
    }
}

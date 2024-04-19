package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.QuoteResponse
import java.util.Locale

data class FamousQuoteModel(
    val owner: String,
    val body: String,
    val imageUrl: String,
) {
    companion object {
        fun QuoteResponse.toDomain(): FamousQuoteModel = FamousQuoteModel(
            owner = owner,
            body = getBodyLocal(quote),
            imageUrl = imageUrl
        )

        private fun getBodyLocal(quotes: List<String>): String = try {
            val local = Locale.getDefault().toString().split("_")[0].uppercase()
            quotes.first { it.startsWith(local) }.split("#")[1]
        } catch (ex: Exception) {
            quotes.first().split("#")[1]
        }
    }
}

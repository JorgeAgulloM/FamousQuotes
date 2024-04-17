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
            body = getBodyLocal(bodyList),
            imageUrl = imageUrl
        )

        private fun getBodyLocal(bodyList: List<String>): String = try {
            val local = Locale.getDefault().toString().split("_")[0]
            local.filter { it.toString().startsWith(local) }.split("#")[1]
        } catch (ex: Exception) {
            bodyList.first()
        }

    }
}

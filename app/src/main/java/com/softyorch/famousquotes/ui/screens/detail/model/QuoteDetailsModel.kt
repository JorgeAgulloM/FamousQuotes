package com.softyorch.famousquotes.ui.screens.detail.model

import com.softyorch.famousquotes.domain.model.FamousQuoteModel

data class QuoteDetailsModel(
    val id: String = "",
    val owner: String = "",
    val body: String = "",
    val imageUrl: String = "",
    val isLiked: Boolean = false,
    val isFavorite: Boolean = false
) {
    companion object {
        fun QuoteDetailsModel.toFamousQuoteModel(): FamousQuoteModel =
            FamousQuoteModel(
                id = id,
                owner = owner,
                body = body,
                imageUrl = imageUrl
            )
    }

    override fun toString(): String {
        return "QuoteDetailsModel(id = $id \n" +
                "owner = $owner" +
                "body = $body" +
                "imageUrl = $imageUrl\n" +
                "isLiked = $isLiked\n" +
                "isFavorite = $isFavorite)"
    }
}

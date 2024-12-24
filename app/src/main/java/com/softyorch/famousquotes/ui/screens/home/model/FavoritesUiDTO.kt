package com.softyorch.famousquotes.ui.screens.home.model

import com.softyorch.famousquotes.domain.model.FavoritesDTO

data class FavoritesUiDTO(
    val id: String,
    val isFavorite: Boolean
) {
    companion object {
        fun FavoritesUiDTO.toDomain(): FavoritesDTO = FavoritesDTO(id, isFavorite)
    }
}

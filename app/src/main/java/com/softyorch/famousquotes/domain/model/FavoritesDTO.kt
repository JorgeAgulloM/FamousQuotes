package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO

data class FavoritesDTO(
    val id: String,
    val isFavorite: Boolean
) {
    companion object {
        fun FavoritesDTO.toDatabase() = FavoritesDataDTO(
            id = id,
            isFavorite = isFavorite
        )
    }
}

package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.FavoritesDTO
import com.softyorch.famousquotes.domain.model.FavoritesDTO.Companion.toDatabase
import javax.inject.Inject

class SetQuoteFavorite @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(updateFavorite: FavoritesDTO) =
        dbService.setQuoteFavorite(updateFavorite.toDatabase()).also {
            if (updateFavorite.isFavorite) setLikeIfNotInList(updateFavorite.id)
        }

    private suspend fun setLikeIfNotInList(id: String) {
        dbService.setQuoteLike(LikesDataDTO(id, true))
    }
}

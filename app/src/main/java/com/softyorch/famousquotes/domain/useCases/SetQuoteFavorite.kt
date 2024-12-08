package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.FavoritesDTO
import com.softyorch.famousquotes.domain.model.FavoritesDTO.Companion.toDatabase
import javax.inject.Inject

class SetQuoteFavorite @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(updateFavorite: FavoritesDTO) =
        dbService.setQuoteFavorite(updateFavorite.toDatabase())
}

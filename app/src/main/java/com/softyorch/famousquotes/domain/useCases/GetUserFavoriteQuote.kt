package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import javax.inject.Inject

class GetUserFavoriteQuote @Inject constructor(private val dbServices: IDatabaseListService) {
    suspend operator fun invoke(id: String) = dbServices.getUserFavoriteQuote(id)
}
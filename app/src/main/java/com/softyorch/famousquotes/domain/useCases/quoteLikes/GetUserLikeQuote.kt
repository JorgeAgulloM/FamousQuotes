package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import javax.inject.Inject

class GetUserLikeQuote @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(id: String) = dbService.getUserLikeQuote(id)
}

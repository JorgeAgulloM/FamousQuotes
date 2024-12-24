package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import javax.inject.Inject

class SetQuoteShown @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(id: String) {
        dbService.setQuoteShown(id)
    }
}

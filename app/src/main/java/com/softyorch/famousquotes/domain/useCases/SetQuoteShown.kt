package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import javax.inject.Inject

class SetQuoteShown @Inject constructor(private val dbService: IDatabaseService) {
    suspend operator fun invoke(id: String) {
        dbService.setQuoteShown(id)
    }
}

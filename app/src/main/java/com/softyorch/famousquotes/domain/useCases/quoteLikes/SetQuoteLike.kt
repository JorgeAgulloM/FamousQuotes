package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.domain.model.LikesDTO
import com.softyorch.famousquotes.domain.model.LikesDTO.Companion.toData
import javax.inject.Inject

class SetQuoteLike @Inject constructor(private val dbService: IDatabaseListService) {
    suspend operator fun invoke(updateLikes: LikesDTO) {
        dbService.setQuoteLike(updateLikes.toData())
    }
}

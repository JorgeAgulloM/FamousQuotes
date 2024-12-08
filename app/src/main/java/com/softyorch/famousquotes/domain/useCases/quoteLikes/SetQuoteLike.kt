package com.softyorch.famousquotes.domain.useCases.quoteLikes

import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.model.LikesDTO
import com.softyorch.famousquotes.domain.model.LikesDTO.Companion.toData
import javax.inject.Inject

class SetQuoteLike @Inject constructor(private val dbService: IDatabaseService) {
    suspend operator fun invoke(updateLikes: LikesDTO) {
        dbService.setQuoteLikes(updateLikes.toData())
    }
}

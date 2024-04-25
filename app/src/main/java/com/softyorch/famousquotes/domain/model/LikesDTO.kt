package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.domain.utils.getTodayId

data class LikesDTO(
    val isLike: Boolean
) {
   companion object {
       fun LikesDTO.toData(): LikesDataDTO = LikesDataDTO(getId(), isLike)
       private fun getId() = getTodayId()
   }
}

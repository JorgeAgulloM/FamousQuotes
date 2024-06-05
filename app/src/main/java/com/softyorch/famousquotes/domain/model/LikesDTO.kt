package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.dto.LikesDataDTO

data class LikesDTO(
    val id: String,
    val isLike: Boolean
) {
   companion object {
       fun LikesDTO.toData(): LikesDataDTO = LikesDataDTO(id, isLike)
   }
}

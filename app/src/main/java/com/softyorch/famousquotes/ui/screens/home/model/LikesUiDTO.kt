package com.softyorch.famousquotes.ui.screens.home.model

import com.softyorch.famousquotes.domain.model.LikesDTO

data class LikesUiDTO(
    val id: String,
    val isLike: Boolean
) {
    companion object {
        fun LikesUiDTO.toDomain(): LikesDTO = LikesDTO(id, isLike)
    }
}

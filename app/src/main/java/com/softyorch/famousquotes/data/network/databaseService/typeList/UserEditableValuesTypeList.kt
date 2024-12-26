package com.softyorch.famousquotes.data.network.databaseService.typeList

import com.softyorch.famousquotes.data.network.databaseService.FAVORITE_QUOTES
import com.softyorch.famousquotes.data.network.databaseService.LIKE_QUOTES
import com.softyorch.famousquotes.data.network.databaseService.SHOWN_QUOTES

sealed interface UserEditableValuesTypeList {
    data class Like(val likes: String = LIKE_QUOTES) : UserEditableValuesTypeList
    data class Shown(val shown: String = SHOWN_QUOTES) : UserEditableValuesTypeList
    data class Favorite(val favorites: String = FAVORITE_QUOTES) : UserEditableValuesTypeList

    companion object {
        fun getList(typeList: UserEditableValuesTypeList) = when (typeList) {
            is Like -> typeList.likes
            is Shown -> typeList.shown
            is Favorite -> typeList.favorites
        }
    }
}

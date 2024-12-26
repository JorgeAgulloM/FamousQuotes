package com.softyorch.famousquotes.data.network.databaseService.typeList

import com.softyorch.famousquotes.data.network.databaseService.FAVORITE_USERS
import com.softyorch.famousquotes.data.network.databaseService.LIKE_USERS
import com.softyorch.famousquotes.data.network.databaseService.SHOWN_USERS

sealed interface QuoteEditableValuesTypeList {
    data class Like(val likes: String = LIKE_USERS) : QuoteEditableValuesTypeList
    data class Shown(val shown: String = SHOWN_USERS) : QuoteEditableValuesTypeList
    data class Favorite(val favorites: String = FAVORITE_USERS) : QuoteEditableValuesTypeList

    companion object {
        fun getList(typeList: QuoteEditableValuesTypeList) = when (typeList) {
            is Like -> typeList.likes
            is Shown -> typeList.shown
            is Favorite -> typeList.favorites
        }
    }
}
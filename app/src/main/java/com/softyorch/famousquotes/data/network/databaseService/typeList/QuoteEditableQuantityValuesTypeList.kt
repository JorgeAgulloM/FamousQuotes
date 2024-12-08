package com.softyorch.famousquotes.data.network.databaseService.typeList

import com.softyorch.famousquotes.data.network.databaseService.FAVORITES
import com.softyorch.famousquotes.data.network.databaseService.LIKES
import com.softyorch.famousquotes.data.network.databaseService.SHOWN

sealed interface QuoteEditableQuantityValuesTypeList {
    data class Likes(val likes: String = LIKES) : QuoteEditableQuantityValuesTypeList
    data class Shown(val shown: String = SHOWN) : QuoteEditableQuantityValuesTypeList
    data class Favorites(val favorites: String = FAVORITES) : QuoteEditableQuantityValuesTypeList

    companion object {
        fun getList(typeList: QuoteEditableQuantityValuesTypeList) = when (typeList) {
            is Likes -> typeList.likes
            is Shown -> typeList.shown
            is Favorites -> typeList.favorites
        }
    }
}
package com.softyorch.famousquotes.data.network.databaseService.auxFireStore

import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
import com.softyorch.famousquotes.data.network.response.UserShownResponse

interface IAuxFireStoreLists {
    suspend fun getUserLikeQuotesId(userId: String): UserLikesResponse?
    suspend fun getUserShownQuotesListId(userId: String): UserShownResponse?
    suspend fun <
            V : QuoteEditableQuantityValuesTypeList,
            U : UserEditableValuesTypeList,
            Q : QuoteEditableValuesTypeList>
            genericModifyData(
        userId: String, id: String, isLike: Boolean, valueList: V, valueUser: U, valueQuote: Q
    )

    suspend fun genericGetQuotesList(
        userId: String,
        typeList: QuoteEditableQuantityValuesTypeList,
        msgError: String
    ): MutableList<QuoteResponse>?
}
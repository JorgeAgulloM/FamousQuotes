package com.softyorch.famousquotes.data.network.databaseService.auxFireStore

import com.google.firebase.firestore.DocumentSnapshot
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserFavoritesResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
import com.softyorch.famousquotes.data.network.response.UserShownResponse
import kotlinx.coroutines.flow.Flow

interface IAuxFireStoreLists {
    suspend fun getUserLikeQuotesId(userId: String): UserLikesResponse?
    suspend fun getUserShownQuotesListId(userId: String): UserShownResponse?
    suspend fun getUserFavoriteQuotesListId(userId: String): UserFavoritesResponse?
    suspend fun <
            V : QuoteEditableQuantityValuesTypeList,
            U : UserEditableValuesTypeList,
            Q : QuoteEditableValuesTypeList>
            selectedTypeModifyData(
        userId: String, id: String, isLike: Boolean, valueList: V, valueUser: U, valueQuote: Q
    )
    suspend fun getSelectedTypeQuotesList(
        userId: String,
        typeList: QuoteEditableQuantityValuesTypeList,
        msgError: String
    ): MutableList<QuoteResponse>?
    suspend fun <T> genericGetDocumentFlow(
        collection: String,
        documentId: String,
        mapResult: (DocumentSnapshot) -> T?
    ): Flow<T?>
}
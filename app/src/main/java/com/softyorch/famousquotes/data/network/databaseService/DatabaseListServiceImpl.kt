package com.softyorch.famousquotes.data.network.databaseService

import android.content.Context
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.IAuxFireStoreLists
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteStatisticsResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseListService
import com.softyorch.famousquotes.utils.userId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseListServiceImpl @Inject constructor(
    private val auxFirebaseLists: IAuxFireStoreLists,
    @ApplicationContext private val context: Context,
) : IDatabaseListService {

    private val userId = context.userId()

    /*************************************** SETTERS ***************************************/


    override suspend fun setQuoteLike(updateLikes: LikesDataDTO) {
        val id = updateLikes.id
        val isLike = updateLikes.isLike

        auxFirebaseLists.selectedTypeModifyData(
            userId = userId,
            quoteId = id,
            isLike = isLike,
            valueList = QuoteEditableQuantityValuesTypeList.Likes(),
            valueUser = UserEditableValuesTypeList.Like(),
            valueQuote = QuoteEditableValuesTypeList.Like()
        )
    }

    override suspend fun setQuoteShown(id: String) {
        auxFirebaseLists.selectedTypeModifyData(
            userId = userId,
            quoteId = id,
            isLike = true,
            valueList = QuoteEditableQuantityValuesTypeList.Shown(),
            valueUser = UserEditableValuesTypeList.Shown(),
            valueQuote = QuoteEditableValuesTypeList.Shown()
        )
    }

    override suspend fun setQuoteFavorite(updateFavorites: FavoritesDataDTO) {
        val id = updateFavorites.id
        val isLike = updateFavorites.isFavorite

        auxFirebaseLists.selectedTypeModifyData(
            userId = userId,
            quoteId = id,
            isLike = isLike,
            valueList = QuoteEditableQuantityValuesTypeList.Favorites(),
            valueUser = UserEditableValuesTypeList.Favorite(),
            valueQuote = QuoteEditableValuesTypeList.Favorite()
        )
    }

    /*************************************** GETTERS ***************************************/

    override suspend fun getUserLikeQuote(id: String): Flow<Boolean?> =
        auxFirebaseLists.genericGetDocumentFlow(
            collection = COLLECTION_USERS,
            documentId = userId
        ) { snapshot ->
            val list = snapshot[LIKE_QUOTES] as? List<*>
            list?.contains(id)
        }

    override suspend fun getUserFavoriteQuote(id: String): Flow<Boolean?> {
        return auxFirebaseLists.genericGetDocumentFlow(
            collection = COLLECTION_USERS,
            documentId = userId
        ) { snapshot ->
            val list = snapshot[FAVORITE_QUOTES] as? List<*>
            list?.contains(id)
        }
    }

    override suspend fun getQuoteStatisticsFlow(id: String): Flow<QuoteStatisticsResponse?> {
        return auxFirebaseLists.genericGetDocumentFlow(
            collection = COLLECTION,
            documentId = id
        ) { snapshot ->
            snapshot.toObject(QuoteStatisticsResponse::class.java)
        }
    }

    override suspend fun getLikeQuotes(): List<QuoteResponse?>? =
        auxFirebaseLists.getSelectedTypeQuotesList(
            userId = userId,
            typeList = QuoteEditableQuantityValuesTypeList.Likes(),
            msgError = "Error getting favorite quotes"
        )

    override suspend fun getShownQuotes(): List<QuoteResponse?>? =
        auxFirebaseLists.getSelectedTypeQuotesList(
            userId = userId,
            typeList = QuoteEditableQuantityValuesTypeList.Shown(),
            msgError = "Error getting quotes shown"
        )

    override suspend fun getFavoriteQuotes(): List<QuoteResponse?>? =
        auxFirebaseLists.getSelectedTypeQuotesList(
            userId = userId,
            typeList = QuoteEditableQuantityValuesTypeList.Favorites(),
            msgError = "Error getting quotes favorites"
        )

}

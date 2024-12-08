package com.softyorch.famousquotes.data.network.databaseService.auxFireStore

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.data.network.databaseService.COLLECTION
import com.softyorch.famousquotes.data.network.databaseService.COLLECTION_USERS
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.utils.throwService
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.databaseService.utils.writeLogServiceError
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
import com.softyorch.famousquotes.data.network.response.UserShownResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuxFireStoreLists(private val firestore: FirebaseFirestore) : IAuxFireStoreLists {

    override suspend fun getUserLikeQuotesId(userId: String): UserLikesResponse? =
        getUserGenericQuotesListId(userId, UserLikesResponse::class.java)

    override suspend fun getUserShownQuotesListId(userId: String): UserShownResponse? =
        getUserGenericQuotesListId(userId, UserShownResponse::class.java)

    override suspend fun <
            V : QuoteEditableQuantityValuesTypeList,
            U : UserEditableValuesTypeList,
            Q : QuoteEditableValuesTypeList>
            genericModifyData(
        userId: String, id: String, isLike: Boolean, valueList: V, valueUser: U, valueQuote: Q
    ) {
        // Añade o quita un like a la lista de likes de la frase
        genericModifyUsersInQuotesQuantity(id, isLike, valueList)

        // Añade o quita una frase a la lista de likes del usuario
        genericModifyQuotesInUserLists(userId, id, isLike, valueUser)

        // Añade o quita un usuario de la lista de la frase
        genericModifyUsersInQuoteLists(userId, id, isLike, valueQuote)
    }

    override suspend fun genericGetQuotesList(
        userId: String,
        typeList: QuoteEditableQuantityValuesTypeList,
        msgError: String
    ): MutableList<QuoteResponse>? = withTimeoutOrNull(FIREBASE_TIMEOUT) {

        val list = when (typeList) {
            is QuoteEditableQuantityValuesTypeList.Likes ->
                getUserLikeQuotesId(userId)?.likeQuotes ?: mutableListOf()
            is QuoteEditableQuantityValuesTypeList.Shown ->
                getUserShownQuotesListId(userId)?.shownQuotes ?: mutableListOf()
            is QuoteEditableQuantityValuesTypeList.Favorites ->
                getUserLikeQuotesId(userId)?.likeQuotes ?: mutableListOf()
        }

        suspendCancellableCoroutine { cancelableCoroutine ->
            if (list.isEmpty()) cancelableCoroutine.resumeWithException(
                throwService("$msgError: Empty list")
            )
            tryCatchFireStore {
                val query = firestore.collection(COLLECTION)
                    .whereIn(FieldPath.documentId(), list)

                query[Source.CACHE].addOnSuccessListener { snapshot ->
                    cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                }.addOnFailureListener {
                    query.get().addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener { ex ->
                        throwService(msgError)
                        cancelableCoroutine.resumeWithException(ex)
                    }
                }
            } ?: cancelableCoroutine.resumeWithException(
                throwService(msgError)
            )
        }
    }

    /***************************************************************************************/
    /*********************************** PRIVATE METHODS ***********************************/
    /***************************************************************************************/

    private suspend fun <T> getUserGenericQuotesListId(userId: String, response: Class<T>): T? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION_USERS).document(userId)
                    document[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObject(response))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(response))
                        }.addOnFailureListener { ex ->
                            writeLogServiceError("Error getting shown quotes", ex)
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting shown quotes")
                )
            }
        }

    private suspend fun genericModifyUsersInQuotesQuantity(
        idQuote: String,
        isLike: Boolean,
        typeList: QuoteEditableQuantityValuesTypeList
    ) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION).document(idQuote)
            firestore.runTransaction { transaction ->
                val snapshot = transaction[document]
                val list = QuoteEditableQuantityValuesTypeList.getList(typeList)
                val currentValue = snapshot.getLong(list) ?: 0

                val changeValueIn = if (isLike) 1 else -1
                val newValue = (currentValue + changeValueIn).coerceAtLeast(0)

                transaction.update(document, list, newValue)
            }.await()
        }
    }

    private suspend fun genericModifyQuotesInUserLists(
        userId: String,
        idQuote: String,
        isNewUser: Boolean,
        typeList: UserEditableValuesTypeList
    ) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION_USERS).document(userId)
            val list = UserEditableValuesTypeList.getList(typeList)
            val fileValue =
                if (isNewUser) FieldValue.arrayUnion(idQuote) else FieldValue.arrayRemove(idQuote)
            document.update(list, fileValue).await()
        }
    }

    private suspend fun genericModifyUsersInQuoteLists(
        userId: String,
        idQuote: String,
        isNewUser: Boolean,
        typeList: QuoteEditableValuesTypeList
    ) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION).document(idQuote)
            val list = QuoteEditableValuesTypeList.getList(typeList)
            val fileValue =
                if (isNewUser) FieldValue.arrayUnion(userId) else FieldValue.arrayRemove(userId)
            document.update(list, fileValue).await()
        }
    }
}

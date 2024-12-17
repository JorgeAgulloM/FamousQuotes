package com.softyorch.famousquotes.data.network.databaseService.auxFireStore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.data.network.databaseService.COLLECTION
import com.softyorch.famousquotes.data.network.databaseService.COLLECTION_USERS
import com.softyorch.famousquotes.data.network.databaseService.CREATED_AT
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.utils.throwService
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.databaseService.utils.writeLogServiceError
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserFavoritesResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
import com.softyorch.famousquotes.data.network.response.UserShownResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuxFireStoreLists(
    private val firestore: FirebaseFirestore,
    private val internetConnection: InternetConnection,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : IAuxFireStoreLists {

    override suspend fun getUserLikeQuotesId(userId: String): UserLikesResponse? =
        getUserGenericQuotesListId(userId, UserLikesResponse::class.java)

    override suspend fun getUserShownQuotesListId(userId: String): UserShownResponse? =
        getUserGenericQuotesListId(userId, UserShownResponse::class.java)

    override suspend fun getUserFavoriteQuotesListId(userId: String): UserFavoritesResponse? =
        getUserGenericQuotesListId(userId, UserFavoritesResponse::class.java)

    override suspend fun <
            V : QuoteEditableQuantityValuesTypeList,
            U : UserEditableValuesTypeList,
            Q : QuoteEditableValuesTypeList>
            selectedTypeModifyData(
        userId: String, quoteId: String, isLike: Boolean, valueList: V, valueUser: U, valueQuote: Q
    ) {
        // Añade o quita una frase a la lista de likes del usuario
        selectedTypeModifyQuotesInUserLists(userId, quoteId, isLike, valueUser) { updated ->
            if (updated)
                selectedTypeModifyUsersInQuotesQuantity(userId, COLLECTION_USERS, isLike, valueList)
        }

        // Añade o quita un usuario de la lista de la frase
        selectedTypeModifyUsersInQuoteLists(userId, quoteId, isLike, valueQuote) { updated ->
            // Añade o quita uno a la lista
            if (updated)
                selectedTypeModifyUsersInQuotesQuantity(quoteId, COLLECTION, isLike, valueList)
        }
    }

    override suspend fun getSelectedTypeQuotesList(
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
                getUserFavoriteQuotesListId(userId)?.favoriteQuotes ?: mutableListOf()
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

    override suspend fun <T> genericGetDocumentFlow(
        collection: String,
        documentId: String,
        mapResult: (DocumentSnapshot) -> T?
    ): Flow<T?> = withTimeoutOrNull(FIREBASE_TIMEOUT) {
        tryCatchFireStore {
            val haveConnection = withContext(dispatcher) {
                internetConnection.isConnectedFlow()
            }

            if (!haveConnection.first()) return@withTimeoutOrNull null

            val document = firestore.collection(collection).document(documentId)

            if (document.get().await() != null && document.get().await().exists()) {
                document.snapshots().map { snapshot ->
                    mapResult(snapshot)
                }
            } else {
                flowOf(null)
            }
        }
    } ?: flowOf(null)

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

    private suspend fun selectedTypeModifyUsersInQuotesQuantity(
        idDocument: String,
        collection: String,
        isLike: Boolean,
        typeList: QuoteEditableQuantityValuesTypeList
    ) {
        tryCatchFireStore {
            val document = firestore.collection(collection).document(idDocument)
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

    private suspend fun selectedTypeModifyQuotesInUserLists(
        userId: String,
        idQuote: String,
        isNewUser: Boolean,
        typeList: UserEditableValuesTypeList,
        onTryUpdate: suspend (Boolean) -> Unit
    ) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION_USERS).document(userId)
            val list = UserEditableValuesTypeList.getList(typeList)

            if (!document.get().await().exists()) {
                document.set(mapOf(CREATED_AT to System.currentTimeMillis())).await()
            }

            if (isNewUser && userDisplayIsAlreadyRegistered(document, list, idQuote)) {
                onTryUpdate(false)
                return
            }

            val fileValue = if (isNewUser) FieldValue.arrayUnion(idQuote)
            else FieldValue.arrayRemove(idQuote)

            document.update(list, fileValue).await()
            if (isNewUser) onTryUpdate(userDisplayIsAlreadyRegistered(document, list, idQuote))
            else onTryUpdate(false)
        } ?: onTryUpdate(false)
    }

    private suspend fun selectedTypeModifyUsersInQuoteLists(
        userId: String,
        idQuote: String,
        isNewUser: Boolean,
        typeList: QuoteEditableValuesTypeList,
        onTryUpdate: suspend (Boolean) -> Unit
    ) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION).document(idQuote)
            val list = QuoteEditableValuesTypeList.getList(typeList)

            if (isNewUser && userDisplayIsAlreadyRegistered(document, list, userId)) {
                onTryUpdate(false)
                return
            }

            val fileValue = if (isNewUser) FieldValue.arrayUnion(userId)
            else FieldValue.arrayRemove(userId)

            document.update(list, fileValue)
            if (isNewUser) onTryUpdate(userDisplayIsAlreadyRegistered(document, list, userId))
            else onTryUpdate(true)

        } ?: onTryUpdate(false)
    }

    private suspend fun userDisplayIsAlreadyRegistered(
        document: DocumentReference,
        list: String,
        userId: String
    ): Boolean {
        val currentList = document.get().await().data?.get(list) as? List<*> ?: emptyList<String>()
        return currentList.contains(userId)
    }
}

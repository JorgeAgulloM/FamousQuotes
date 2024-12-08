package com.softyorch.famousquotes.data.network.databaseService

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.IAuxFireStoreLists
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableQuantityValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.QuoteEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.typeList.UserEditableValuesTypeList
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.databaseService.utils.writeLogServiceError
import com.softyorch.famousquotes.data.network.dto.FavoritesDataDTO
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikesQuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.utils.generateRandomId
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auxFirebaseLists: IAuxFireStoreLists,
    private val internetConnection: InternetConnection,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    @ApplicationContext private val context: Context,
) : IDatabaseService {

    //Provisional
    @SuppressLint("HardwareIds")
    private val userId = Settings.Secure.getString(context.contentResolver, ANDROID_ID)

    override suspend fun getQuote(id: String): QuoteResponse? = genericGetQuote(id)

    override suspend fun getRandomQuote(): QuoteResponse? = genericGetQuote(null)

    // OJO CON USAR ESTO!!!!!!!!!
    override suspend fun getAllQuotes(): List<QuoteResponse?>? = genericGetAllQuotes()

    override suspend fun setQuoteLikes(updateLikes: LikesDataDTO) {
        val id = updateLikes.id
        val isLike = updateLikes.isLike

        auxFirebaseLists.genericModifyData(
            userId = userId,
            id = id,
            isLike = isLike,
            valueList = QuoteEditableQuantityValuesTypeList.Likes(),
            valueUser = UserEditableValuesTypeList.Like(),
            valueQuote = QuoteEditableValuesTypeList.Like()
        )
    }

    override suspend fun setQuoteShown(id: String) {
        auxFirebaseLists.genericModifyData(
            userId = userId,
            id = id,
            isLike = true,
            valueList = QuoteEditableQuantityValuesTypeList.Shown(),
            valueUser = UserEditableValuesTypeList.Shown(),
            valueQuote = QuoteEditableValuesTypeList.Shown()
        )
    }

    override suspend fun setQuoteFavorite(updateFavorites: FavoritesDataDTO) {
        val id = updateFavorites.id
        val isLike = updateFavorites.isFavorite

        auxFirebaseLists.genericModifyData(
            userId = userId,
            id = id,
            isLike = isLike,
            valueList = QuoteEditableQuantityValuesTypeList.Favorites(),
            valueUser = UserEditableValuesTypeList.Favorite(),
            valueQuote = QuoteEditableValuesTypeList.Favorite()
        )
    }

    override suspend fun getLikesQuoteFlow(id: String): Flow<LikesQuoteResponse?> =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            tryCatchFireStore {
                val haveConnection = withContext(dispatcher) {
                    internetConnection.isConnectedFlow()
                }

                if (!haveConnection.first()) return@withTimeoutOrNull null

                val document = firestore.collection(COLLECTION).document(id)

                if (document.get().await() != null)
                    if (document.get().await().exists())
                        document.snapshots().map { doc ->
                            val result = doc.toObject(LikesQuoteResponse::class.java)
                            result?.let { LikesQuoteResponse(likes = it.likes) }
                        } else null
                else null
            }
        } ?: flowOf(LikesQuoteResponse())

    override suspend fun getUserLikeQuote(id: String): Flow<Boolean?> =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            tryCatchFireStore {
                val haveConnection = withContext(dispatcher) {
                    internetConnection.isConnectedFlow()
                }

                if (!haveConnection.first()) return@withTimeoutOrNull null

                val document = firestore.collection(COLLECTION_USERS).document(userId)

                if (document.get().await() != null)
                    if (document.get().await().exists())
                        document.snapshots().map { doc ->
                            val list = doc[LIKE_QUOTES] as? List<*>
                            list?.contains(id)
                        } else null
                else null
            }
        } ?: flowOf(false)

    override suspend fun getLikeQuotes(): List<QuoteResponse?>? =
        auxFirebaseLists.genericGetQuotesList(
            userId = userId,
            typeList = QuoteEditableQuantityValuesTypeList.Likes(),
            msgError = "Error getting favorite quotes"
        )


    override suspend fun getQuotesShown(): List<QuoteResponse?>? =
        auxFirebaseLists.genericGetQuotesList(
            userId = userId,
            typeList = QuoteEditableQuantityValuesTypeList.Shown(),
            msgError = "Error getting quotes shown"
        )

    /***************************************************************************************/
    /*********************************** PRIVATE METHODS ***********************************/
    /***************************************************************************************/

    private suspend fun genericGetQuote(id: String? = null): QuoteResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val documentId = id ?: generateRandomId().also {
                        writeLog(text = "randomId: $it")
                    }

                    val document = firestore.collection(COLLECTION).document(documentId)
                    document[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObject(QuoteResponse::class.java))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(QuoteResponse::class.java))
                        }.addOnFailureListener { ex ->
                            writeLogServiceError("Error getting quote", ex)
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    auxFirebaseLists.throwService("Error getting quote")
                )
            }
        }

    private suspend fun genericGetAllQuotes(): List<QuoteResponse?>? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION)
                    document[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                        }.addOnFailureListener { ex ->
                            writeLogServiceError("Error getting all quotes", ex)
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    auxFirebaseLists.throwService("Error getting all quotes")
                )
            }
        }

}

package com.softyorch.famousquotes.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.data.network.response.LikeResponse
import com.softyorch.famousquotes.data.network.response.LikesQuoteResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
import com.softyorch.famousquotes.data.network.response.UserShownResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.utils.generateRandomId
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
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
    private val internetConnection: InternetConnection,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    @ApplicationContext private val context: Context,
) : IDatabaseService {

    //Provisional
    @SuppressLint("HardwareIds")
    private val userId = Settings.Secure.getString(context.contentResolver, ANDROID_ID)

    override suspend fun getQuote(id: String): QuoteResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION).document(id)
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
                    Throwable("Error getting quote")
                )
            }
        }

    override suspend fun getRandomQuote(): QuoteResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val randomId = generateRandomId().also { writeLog(text = "randomId: $it") }
                    firestore.collection(COLLECTION).document(randomId).get().addOnSuccessListener {
                        cancelableCoroutine.resume(
                            it.toObject(QuoteResponse::class.java)
                        )
                    }.addOnFailureListener {
                        writeLogServiceError("Error getting random quotes", it)
                        cancelableCoroutine.resumeWithException(it)
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting random quotes")
                )
            }
        }

    override suspend fun likeDislikeQuote(updateLikes: LikesDataDTO) {
        val id = updateLikes.id
        val isLike = updateLikes.isLike
        val likeQuotes = getLikeQuotes()?.likeQuotes?.toMutableList() ?: mutableListOf()

        if (isLike) likeQuotes.add(id) else likeQuotes.remove(id)
        likeQuotes.map { it.toLong() }.sorted().map { it.toString() }

        writeLog(INFO, "$SERVICE_NAME Changed like to: $updateLikes")

        // Añade o quita un like a la lista de likes de la frase
        modifyLikeValue(id, isLike)

        // Añade o quita una frase a la lista de likes del usuario
        modifyUserOnQuoteGenericList(id, isLike, UserEditableValuesTypeList.Like())

        // Añade o quita un usuario de la lista de la frase
        modifyQuotesOnUserGenericList(id, isLike, QuoteEditableValuesTypeList.Like())
    }

    /* Crea un guardado de los usuario s que han visto la frase, que le han dado Like, y añade una lista de favoritos
    * To-do esto añadelo a listas en el documento de la quote, más lo correspondiente para el usuario.
    *  */

    override suspend fun setQuoteShown(id: String) {
        tryCatchFireStore {
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .update(SHOWN_QUOTES, FieldValue.arrayUnion(id))
                .await()
        }
    }

    override suspend fun getLikeQuoteFlow(id: String): Flow<LikeQuoteResponse?> =
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
                            val result = doc.toObject(LikeQuoteResponse::class.java)

                            val isLike = getUserIsLike(id).like

                            result?.let {
                                LikeQuoteResponse(id = it.id, likes = it.likes, like = isLike)
                            }
                        } else null
                else null
            }
        } ?: flowOf(LikeQuoteResponse())

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

    // OJO CON USAR ESTO!!!!!!!!!
    override suspend fun getAllQuotes(): List<QuoteResponse?>? =
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
                    throwService("Error getting all quotes")
                )
            }
        }

    override suspend fun getFavoriteQuotes(): List<QuoteResponse?>? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            val likes = getLikeQuotes()?.likeQuotes ?: mutableListOf()
            suspendCancellableCoroutine { cancelableCoroutine ->
                if (likes.isEmpty()) cancelableCoroutine.resumeWithException(
                    throwService("Error getting favorite quotes: Empty list")
                )
                tryCatchFireStore {
                    val query = firestore.collection(COLLECTION)
                        .whereIn(FieldPath.documentId(), likes)

                    query[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener {
                        query.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                        }.addOnFailureListener { ex ->
                            throwService("Error getting favorite quotes")
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting favorite quotes")
                )
            }
        }

    override suspend fun getQuotesShown(): List<QuoteResponse?>? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            val shownList = getQuotesShownList()?.shownQuotes ?: mutableListOf()
            suspendCancellableCoroutine { cancelableCoroutine ->
                if (shownList.isEmpty()) cancelableCoroutine.resumeWithException(
                    throwService("Error getting shown quotes: Empty list")
                )
                tryCatchFireStore {
                    val query = firestore.collection(COLLECTION)
                        .whereIn(FieldPath.documentId(), shownList)

                    query[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener {
                        query.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                        }.addOnFailureListener { ex ->
                            throwService("Error getting quotes shown")
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting quotes shown")
                )
            }
        }

    /***************************************************************************************/
    /*********************************** PRIVATE METHODS ***********************************/
    /***************************************************************************************/

    private suspend fun modifyLikeValue(id: String, isLike: Boolean) {
        tryCatchFireStore {
            val document = firestore.collection(COLLECTION).document(id)
            firestore.runTransaction { transaction ->
                val snapshot = transaction[document]
                val currentValue = snapshot.getLong(LIKES) ?: 0

                val changeValueIn = if (isLike) 1 else -1
                val newValue = (currentValue + changeValueIn).coerceAtLeast(0)

                transaction.update(document, LIKES, newValue)
            }.await()
        }
    }

    private suspend fun modifyUserOnQuoteGenericList(
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

    private suspend fun modifyQuotesOnUserGenericList(
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

    private suspend fun getUserIsLike(id: String): LikeResponse {
        return tryCatchFireStore {
            firestore.collection(COLLECTION_LIKES)
                .document(id)
                .collection(COLLECTION_USERS_LIKE)
                .document(userId).get().await().toObject(LikeResponse::class.java) ?: LikeResponse()
        } ?: LikeResponse()
    }

    private suspend fun getLikeQuotes(): UserLikesResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION_USERS).document(userId)
                    document[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObject(UserLikesResponse::class.java))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(UserLikesResponse::class.java))
                        }.addOnFailureListener { ex ->
                            writeLogServiceError("Error getting like quotes", ex)
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting like quotes")
                )
            }
        }

    private suspend fun getQuotesShownList(): UserShownResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION_USERS).document(userId)
                    document[Source.CACHE].addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObject(UserShownResponse::class.java))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(UserShownResponse::class.java))
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

    private inline fun <T> tryCatchFireStore(onTry: () -> T): T? = try {
        onTry()
    } catch (fFex: FirebaseFirestoreException) {
        writeLogServiceError("Firebase firestore Error", fFex)
        null
    } catch (fex: FirebaseException) {
        writeLogServiceError("Firebase Error", fex)
        null
    } catch (ex: Exception) {
        writeLogServiceError("DataBase Service Error", ex)
        null
    }

    private fun writeLogServiceError(message: String, ex: Exception) {
        writeLog(ERROR, "$SERVICE_NAME $message: ${ex.cause} - Message: ${ex.message}", ex)
    }

    private fun throwService(message: String) = Throwable("$SERVICE_NAME $message")

    companion object {
        const val COLLECTION = BuildConfig.DB_COLLECTION
        private const val COLLECTION_LIKES = "${COLLECTION}_likes"
        private const val COLLECTION_USERS = "${COLLECTION}_users"
        private const val COLLECTION_USERS_LIKE = "users_like"
        private const val SERVICE_NAME = "FireStore ->"
        private const val LIKES = "likes"
        private const val LIKE_USERS = "likeUsers"
        private const val SHOWN_USERS = "shownUsers"
        private const val FAVORITE_USERS = "favoriteUsers"
        private const val LIKE_QUOTES = "likeQuotes"
        private const val SHOWN_QUOTES = "shownQuotes"
        private const val FAVORITE_QUOTES = "favoriteQuotes"

        sealed interface QuoteEditableValuesTypeList {
            data class Like(val value: String = LIKE_USERS) : QuoteEditableValuesTypeList
            data class Shown(val value: String = SHOWN_USERS) : QuoteEditableValuesTypeList
            data class Favorite(val value: String = FAVORITE_USERS) : QuoteEditableValuesTypeList

            companion object {
                fun getList(typeList: QuoteEditableValuesTypeList) = when (typeList) {
                    is Like -> typeList.value
                    is Shown -> typeList.value
                    is Favorite -> typeList.value
                }
            }
        }

        sealed interface UserEditableValuesTypeList {
            data class Like(val value: String = LIKE_QUOTES) : UserEditableValuesTypeList
            data class Shown(val value: String = SHOWN_QUOTES) : UserEditableValuesTypeList
            data class Favorite(val value: String = FAVORITE_QUOTES) : UserEditableValuesTypeList

            companion object {
                fun getList(typeList: UserEditableValuesTypeList) = when (typeList) {
                    is Like -> typeList.value
                    is Shown -> typeList.value
                    is Favorite -> typeList.value
                }
            }
        }
    }

}

package com.softyorch.famousquotes.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldPath
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
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.data.network.response.UserLikesResponse
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
        val quoteLikes = getLikeQuote(updateLikes.id)
        val id = updateLikes.id
        val likes = quoteLikes?.likes ?: 0
        val newLikes = likes + if (updateLikes.isLike) 1 else if (likes > 0) -1 else 0
        val isLike = updateLikes.isLike
        val likeQuotes = getLikeQuotes()?.likeQuotes?.toMutableList() ?: mutableListOf()

        if (isLike) likeQuotes.add(id) else likeQuotes.remove(id)
        likeQuotes.map { it.toLong() }.sorted().map { it.toString() }

        writeLog(INFO, "$SERVICE_NAME Changed like to: $updateLikes")

        tryCatchFireStore {
            // Prepare likes
            val newData = hashMapOf(
                ID to id,
                LIKES to newLikes
            )

            // Prepare user like
            val userLike = hashMapOf(
                ID to userId,
                LIKE to isLike
            )

            // Prepare user likes
            val newDataUser = hashMapOf(
                LIKE_QUOTES to likeQuotes
            )

            // Set values
            firestore.collection(COLLECTION_LIKES).document(id).apply {
                set(newData)
                collection(COLLECTION_USERS_LIKE).document(userId).set(userLike)
            }
            firestore.collection(COLLECTION_USERS).document(userId).set(newDataUser)
        }
    }

    override suspend fun getLikeQuoteFlow(id: String): Flow<LikeQuoteResponse?> = withTimeoutOrNull(FIREBASE_TIMEOUT) {
        tryCatchFireStore {
            val haveConnection = withContext(dispatcher) {
                internetConnection.isConnectedFlow()
            }

            if (!haveConnection.first()) return@withTimeoutOrNull null

            val document = firestore.collection(COLLECTION_LIKES).document(id)

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

    // OJO CON USAR ESTO!!!!!!!!!
    override suspend fun getAllQuotes(): List<QuoteResponse?>? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    val document = firestore.collection(COLLECTION)
                    document.get().addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener { ex ->
                        writeLogServiceError("Error getting all quotes", ex)
                        cancelableCoroutine.resumeWithException(ex)
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

                    query.get().addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObjects(QuoteResponse::class.java))
                    }.addOnFailureListener { ex ->
                        throwService("Error getting favorite quotes")
                        cancelableCoroutine.resumeWithException(ex)
                    }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting favorite quotes")
                )
            }
        }

    /***************************************************************************************/
    /*********************************** PRIVATE METHODS ***********************************/
    /***************************************************************************************/

    private suspend fun getLikeQuote(id: String): LikeQuoteResponse? {
        return tryCatchFireStore {
            val document = firestore.collection(COLLECTION_LIKES).document(id)

            if (document.get().await().exists()) {
                val likeQuote = document.get().await().toObject(LikeQuoteResponse::class.java)
                likeQuote?.copy(like = getUserIsLikeNew().likeQuotes?.contains(id) == true)
            } else null
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

    private suspend fun getUserIsLikeNew(): UserLikesResponse {
        return tryCatchFireStore {
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
                .toObject(UserLikesResponse::class.java) ?: UserLikesResponse()
        } ?: UserLikesResponse()
    }

    private suspend fun getLikeQuotes(): UserLikesResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    firestore.collection(COLLECTION_USERS).document(userId).get()
                        .addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(UserLikesResponse::class.java))
                        }.addOnFailureListener { ex ->
                            writeLogServiceError("Error getting like quotes", ex)
                            cancelableCoroutine.resumeWithException(ex)
                        }
                } ?: cancelableCoroutine.resumeWithException(
                    throwService("Error getting like quotes")
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
        private const val ID = "id"
        private const val LIKE = "like"
        private const val LIKES = "likes"
        private const val LIKE_QUOTES = "likeQuotes"
    }

}

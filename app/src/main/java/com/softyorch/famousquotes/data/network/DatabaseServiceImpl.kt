package com.softyorch.famousquotes.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.snapshots
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.data.network.response.LikeResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.utils.generateRandomId
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
) : IDatabaseService {

    companion object {
        const val COLLECTION = BuildConfig.DB_COLLECTION
        private const val COLLECTION_LIKES = "${COLLECTION}_likes"
        private const val COLLECTION_USERS_LIKE = "users_like"
    }

    //Provisional
    @SuppressLint("HardwareIds")
    private val userId = Settings.Secure.getString(context.contentResolver, ANDROID_ID)

    override suspend fun getQuote(id: String): QuoteResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            try {
                suspendCancellableCoroutine { cancelableCoroutine ->
                    val document = firestore.collection(COLLECTION).document(id)
                    document.get(Source.CACHE).addOnSuccessListener { snapshot ->
                        cancelableCoroutine.resume(snapshot.toObject(QuoteResponse::class.java))
                    }.addOnFailureListener {
                        document.get().addOnSuccessListener { snapshot ->
                            cancelableCoroutine.resume(snapshot.toObject(QuoteResponse::class.java))
                        }.addOnFailureListener { ex ->
                            cancelableCoroutine.resumeWithException(ex)
                        }
                    }
                }
            } catch (ex: FirebaseException) {
                writeLog(ERROR, "Error from Firebase Firestore: ${ex.cause}")
                null
            }
        }

    override suspend fun getRandomQuote(): QuoteResponse? =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                try {
                    val randomId = generateRandomId().also { writeLog(text = "randomId: $it") }
                    firestore.collection(COLLECTION).document(randomId).get().addOnSuccessListener {
                        cancelableCoroutine.resume(
                            it.toObject(QuoteResponse::class.java)
                        )
                    }.addOnFailureListener {
                        writeLog(ERROR, "Error listen from Firebase Firestore: ${it.cause}")
                        cancelableCoroutine.resumeWithException(it)
                    }
                } catch (ex: FirebaseException) {
                    writeLog(ERROR, "Error from Firebase Firestore: ${ex.cause}")
                    cancelableCoroutine.resume(null)
                    cancelableCoroutine.resumeWithException(ex)
                }
            }
        }

    override suspend fun likeDislikeQuote(updateLikes: LikesDataDTO) {
        val quoteLikes = getLikeQuote(updateLikes.id)
        val id = updateLikes.id
        val likes = quoteLikes?.likes ?: 0
        val newLikes = likes + if (updateLikes.isLike) 1 else if (likes > 0) -1 else 0
        val isLike = updateLikes.isLike

        writeLog(INFO, "Changed like to: $updateLikes")

        val newData = hashMapOf(
            "id" to id,
            "likes" to newLikes
        )

        val document = firestore.collection(COLLECTION_LIKES).document(id)
        document.set(newData)

        val userLike = hashMapOf(
            "id" to userId,
            "like" to isLike
        )
        document.collection(COLLECTION_USERS_LIKE).document(userId).set(userLike)
    }

    override suspend fun getLikeQuoteFlow(id: String): Flow<LikeQuoteResponse?> {
        return withTimeoutOrNull(FIREBASE_TIMEOUT) {
            try {
                val document = firestore.collection(COLLECTION_LIKES).document(id)

                if (document.get().await().exists()) document.snapshots().map { doc ->
                    val result = doc.toObject(LikeQuoteResponse::class.java)
                    val isLike = document.collection(COLLECTION_USERS_LIKE)
                        .document(userId)
                        .get()
                        .await().toObject(LikeResponse::class.java)?.like ?: false

                    result?.let { LikeQuoteResponse(id = it.id, likes = it.likes, like = isLike) }
                } else null

            } catch (ex: Exception) {
                writeLog(ERROR, "Error from Firebase Firestore: ${ex.cause}")
                null
            }
        } ?: flowOf(LikeQuoteResponse())
    }

    private suspend fun getLikeQuote(id: String): LikeQuoteResponse? {
        try {
            val document = firestore.collection(COLLECTION_LIKES).document(id)

            return if (document.get().await().exists()) {
                val likeQuote = document.get().await().toObject(LikeQuoteResponse::class.java)
                likeQuote?.copy(like = getUserIsLike(id).like.also {
                    writeLog(
                        INFO,
                        "[getUserIsLike] -> $it"
                    )
                })
            } else null

        } catch (ex: Exception) {
            writeLog(ERROR, "Error from Firebase Firestore: ${ex.cause}")
            return null
        }
    }

    private suspend fun getUserIsLike(id: String): LikeResponse {
        return firestore.collection(COLLECTION_LIKES)
            .document(id)
            .collection(COLLECTION_USERS_LIKE)
            .document(userId).get().await().toObject(LikeResponse::class.java) ?: LikeResponse()
    }
}

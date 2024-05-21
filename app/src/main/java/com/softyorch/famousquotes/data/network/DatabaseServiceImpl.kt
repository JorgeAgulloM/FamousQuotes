package com.softyorch.famousquotes.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.data.network.response.LikeResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
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
        private const val TIMEOUT: Long = 4000L
    }

    //Provisional
    @SuppressLint("HardwareIds")
    private val userId = Settings.Secure.getString(context.contentResolver, ANDROID_ID)

    override suspend fun getQuote(id: String): QuoteResponse? =
        withTimeoutOrNull(TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                val document = firestore.collection(COLLECTION).document(id)
                document.get().addOnSuccessListener {
                    cancelableCoroutine.resume(it.toObject(QuoteResponse::class.java))
                }.addOnFailureListener {
                    cancelableCoroutine.resumeWithException(it)
                }
            }
        }

    override suspend fun getRandomQuote(): QuoteResponse? =
        withTimeoutOrNull(TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                firestore.collection(COLLECTION).orderBy("id").get().addOnSuccessListener {
                    cancelableCoroutine.resume(
                        it.map { snapshot ->
                            snapshot.toObject(QuoteResponse::class.java)
                        }.random()
                    )
                }.addOnFailureListener {
                    cancelableCoroutine.resumeWithException(it)
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
        return withTimeoutOrNull(TIMEOUT) {
            val document = firestore.collection(COLLECTION_LIKES).document(id)
            document.snapshots().map { doc ->
                val result = doc.toObject(LikeQuoteResponse::class.java)
                val isLike = document.collection(COLLECTION_USERS_LIKE)
                    .document(userId)
                    .get()
                    .await().toObject(LikeResponse::class.java)?.like ?: false

                result?.let { LikeQuoteResponse(id = it.id, likes = it.likes, like = isLike ) }
            }
        } ?: flowOf(LikeQuoteResponse())
    }

    private suspend fun getLikeQuote(id: String): LikeQuoteResponse? {
        val document = firestore.collection(COLLECTION_LIKES).document(id)
        val likeQuote = document.get().await().toObject(LikeQuoteResponse::class.java)
        return likeQuote?.copy(like = getUserIsLike(id).like.also { writeLog(INFO, "[getUserIsLike] -> $it") })
    }

    private suspend fun getUserIsLike(id: String): LikeResponse {
        return firestore.collection(COLLECTION_LIKES)
            .document(id)
            .collection(COLLECTION_USERS_LIKE)
            .document(userId).get().await().toObject(LikeResponse::class.java) ?: LikeResponse()
    }
}

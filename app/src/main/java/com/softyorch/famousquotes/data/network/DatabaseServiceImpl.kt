package com.softyorch.famousquotes.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.snapshots
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.network.dto.LikesDataDTO
import com.softyorch.famousquotes.data.network.response.LikeQuoteResponse
import com.softyorch.famousquotes.data.network.response.LikeResponse
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.LevelLog.WARN
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

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

    override suspend fun getQuote(id: String): QuoteResponse? {
//      getAllQuotes()

        val document = firestore.collection(COLLECTION).document(id)

        return try {
            document.get(Source.CACHE).await().toObject(QuoteResponse::class.java)
        } catch (ex: Exception) {
            writeLog(WARN, "Fail to get image $id from cache!!")
            document.get().await().toObject(QuoteResponse::class.java)
        }
    }

    override suspend fun getRandomQuote(): QuoteResponse? {
        val document = getAllQuotes()
        return document.random()
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
        val document = firestore.collection(COLLECTION_LIKES).document(id)
        return document.snapshots().map { doc ->
            val result = doc.toObject(LikeQuoteResponse::class.java)
            val isLike = document.collection(COLLECTION_USERS_LIKE)
                .document(userId)
                .get()
                .await().toObject(LikeResponse::class.java)?.like ?: false

            result?.let { LikeQuoteResponse(id = it.id, likes = it.likes, like = isLike ) }
        }
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

    private suspend fun getAllQuotes(): List<QuoteResponse?> {
        val document = firestore.collection(COLLECTION).orderBy("id").get().await().map {
            it.toObject(QuoteResponse::class.java)
        }
//      setUpdate(document)
        return document
    }

    private suspend fun setUpdate(quotes: List<QuoteResponse>) {

        var day = 1
        quotes.forEach { quote ->
            val id = getIdFromCalendarStartZero(day)
            val date = getToday(id.toLong())

            day += 1
            val newData = hashMapOf(
                "id" to id,
                "owner" to quote.owner,
                "quote" to listOf(quote.quote[0]),
                "imageUrl" to "",
                "date" to date
            )

            firestore.collection(COLLECTION).document(id).set(newData).await()
        }
    }

    private fun getToday(timeInMillis: Long) = Timestamp((timeInMillis / 1000), 0)

    private fun getIdFromCalendarStartZero(day: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.DAY_OF_YEAR, day)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val oneHourInMillis = 1000 * 60 * 60
        return (calendar.timeInMillis - oneHourInMillis).toString()
    }

}

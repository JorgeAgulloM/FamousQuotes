package com.softyorch.famousquotes.data.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.FakeDataBaseImplement
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.IDatabaseService
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): IDatabaseService {

    companion object {
        const val COLLECTION = BuildConfig.DB_COLLECTION
        const val DOCUMENT = "quote_objects"
    }

    override suspend fun getQuote(id: String): QuoteResponse? {
        return firestore.collection(COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject(QuoteResponse::class.java)
    }


    private fun getImageFromStorage() {

    }





    suspend fun provisionalSetQuotes() {
        val quotes = FakeDataBaseImplement().quotes

        val filterQuotes = quotes.distinctBy { it.body }

        Log.i("LOGTAG", "quotes.size: ${filterQuotes.size}")
        filterQuotes.forEach { quote ->

                val id = generateId()
                val productQuote = hashMapOf(
                    "id" to id,
                    "owner" to quote.title,
                    "bodyList" to listOf("ES#${quote.body}"),
                    "imageUrl" to ""
                )

                firestore.collection(COLLECTION).document(id).set(productQuote).await()
            }
    }

    private var temporalId: Long = 0

    private fun generateId(): String {
        if (temporalId == 0L) {
            val calendar = Calendar.getInstance()
            calendar.set(2024, Calendar.YEAR, 1, 0, 0,0)
            calendar.set(Calendar.MILLISECOND, 0)

            temporalId = calendar.timeInMillis
        } else {
            val oneDayInMillis = 1000 * 60 * 60 * 24
            temporalId += oneDayInMillis
        }

        return temporalId.toString()
    }


    fun provisionalSetImages() {

    }

}
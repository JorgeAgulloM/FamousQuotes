package com.softyorch.famousquotes.data.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): IDatabaseService {

    companion object {
        const val COLLECTION = BuildConfig.DB_COLLECTION
    }

    override suspend fun getQuote(id: String): QuoteResponse? {
        val document = firestore.collection(COLLECTION).document(id)

        return try {
            document.get(Source.CACHE).await().toObject(QuoteResponse::class.java)
        } catch (ex: Exception) {
            document.get().await().toObject(QuoteResponse::class.java)
        }
    }
}

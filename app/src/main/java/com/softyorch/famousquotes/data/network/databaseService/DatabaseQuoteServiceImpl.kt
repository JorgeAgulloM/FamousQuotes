package com.softyorch.famousquotes.data.network.databaseService

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.data.network.databaseService.utils.throwService
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.databaseService.utils.writeLogServiceError
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.utils.generateRandomId
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DatabaseQuoteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IDatabaseQuoteService {

    override suspend fun getQuoteFlow(id: String?): Flow<QuoteResponse?> = genericGetQuoteFlow(id)

    override suspend fun getRandomQuote(): Flow<QuoteResponse?> = genericGetQuoteFlow()

    // OJO CON USAR ESTO!!!!!!!!!
    override suspend fun getAllQuotes(): List<QuoteResponse?>? = genericGetAllQuotes()

    /***************************************************************************************/
    /*********************************** PRIVATE METHODS ***********************************/
    /***************************************************************************************/

    private suspend fun genericGetQuoteFlow(id: String? = null): Flow<QuoteResponse?> =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            tryCatchFireStore {
                val documentId = id ?: generateRandomId().also {
                    writeLog(text = "randomId: $it")
                }

                val document = firestore.collection(COLLECTION).document(documentId)

                if (document.get().await() != null && document.get().await().exists()) {
                    document.snapshots().map { snapshot ->
                        snapshot.toObject(QuoteResponse::class.java)
                    }
                } else {
                    flowOf(null)
                }
            }
        } ?: flowOf(null)

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
                    throwService("Error getting all quotes")
                )
            }
        }

}

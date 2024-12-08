package com.softyorch.famousquotes.data.network.databaseService

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.data.network.databaseService.utils.throwService
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.databaseService.utils.writeLogServiceError
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseQuoteService
import com.softyorch.famousquotes.domain.utils.generateRandomId
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DatabaseQuoteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IDatabaseQuoteService {

    override suspend fun getQuote(id: String): QuoteResponse? = genericGetQuote(id)

    override suspend fun getRandomQuote(): QuoteResponse? = genericGetQuote(null)

    // OJO CON USAR ESTO!!!!!!!!!
    override suspend fun getAllQuotes(): List<QuoteResponse?>? = genericGetAllQuotes()

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
                    throwService("Error getting quote")
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
                    throwService("Error getting all quotes")
                )
            }
        }

}

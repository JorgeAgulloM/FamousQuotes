package com.softyorch.famousquotes.data.network

import com.google.firebase.messaging.FirebaseMessaging
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.data.network.databaseService.utils.tryCatchFireStore
import com.softyorch.famousquotes.data.network.response.SubscribeNotificationsResponse
import com.softyorch.famousquotes.domain.interfaces.ISubscribeNotificationsByTopic
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

class SubscribeNotificationsByTopicImpl @Inject constructor(
    private val messaging: FirebaseMessaging
) : ISubscribeNotificationsByTopic {
    override suspend fun subscribeToTopic(topic: String): SubscribeNotificationsResponse =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    messaging.subscribeToTopic(topic)
                        .addOnCompleteListener { task ->
                            cancelableCoroutine.resume(SubscribeNotificationsResponse(task.isSuccessful))
                        }.addOnFailureListener {
                            cancelableCoroutine.resume(SubscribeNotificationsResponse(false))
                        }
                } ?: cancelableCoroutine.resume(SubscribeNotificationsResponse(false))
            }
        } ?: SubscribeNotificationsResponse(false)

    override suspend fun unsubscribeFromTopic(topic: String): SubscribeNotificationsResponse =
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                tryCatchFireStore {
                    messaging.unsubscribeFromTopic(topic)
                        .addOnCompleteListener { task ->
                            cancelableCoroutine.resume(SubscribeNotificationsResponse(task.isSuccessful))
                        }.addOnFailureListener {
                            cancelableCoroutine.resume(SubscribeNotificationsResponse(false))
                        }
                } ?: cancelableCoroutine.resume(SubscribeNotificationsResponse(false))
            }
        } ?: SubscribeNotificationsResponse(false)
}

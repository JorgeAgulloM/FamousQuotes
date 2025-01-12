package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.network.response.SubscribeNotificationsResponse

interface ISubscribeNotificationsByTopic {
    suspend fun subscribeToTopic(topic: String): SubscribeNotificationsResponse
    suspend fun unsubscribeFromTopic(topic: String): SubscribeNotificationsResponse
}

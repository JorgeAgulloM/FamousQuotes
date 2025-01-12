package com.softyorch.famousquotes.domain.model

import com.softyorch.famousquotes.data.network.response.SubscribeNotificationsResponse

data class SubscribeNotificationModel(
    val subscribe: Boolean,
) {
    companion object {
        fun SubscribeNotificationsResponse.toDomain() = SubscribeNotificationModel(subscribe)
    }
}

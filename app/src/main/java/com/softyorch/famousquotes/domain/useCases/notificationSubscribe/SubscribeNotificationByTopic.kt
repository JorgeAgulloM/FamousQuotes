package com.softyorch.famousquotes.domain.useCases.notificationSubscribe

import com.softyorch.famousquotes.domain.model.SubscribeNotificationDTO
import com.softyorch.famousquotes.domain.interfaces.ISubscribeNotificationsByTopic
import com.softyorch.famousquotes.domain.model.SubscribeNotificationModel
import com.softyorch.famousquotes.domain.model.SubscribeNotificationModel.Companion.toDomain
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import javax.inject.Inject

class SubscribeNotificationByTopic @Inject constructor(
    private val subscribe: ISubscribeNotificationsByTopic
) {
    suspend operator fun invoke(dto: SubscribeNotificationDTO): SubscribeNotificationModel =
        if (dto.subscribe) subscribeToTopic(dto.topic) else unsubscribeFromTopic(dto.topic)

    private suspend fun subscribeToTopic(topic: String): SubscribeNotificationModel =
        subscribe.subscribeToTopic(topic).toDomain().also { CreateLog(it, topic, true) }

    private suspend fun unsubscribeFromTopic(topic: String): SubscribeNotificationModel =
        subscribe.unsubscribeFromTopic(topic).toDomain().also { CreateLog(it, topic) }

    private fun CreateLog(
        it: SubscribeNotificationModel,
        topic: String,
        isSubscribed: Boolean = false
    ) {
        val text = if (isSubscribed) "Subscribed to" else "Unsubscribed to"
        if (it.subscribe) writeLog(LevelLog.INFO, text = "$text $topic")
        else writeLog(LevelLog.ERROR, text = "Error $text $topic")
    }
}

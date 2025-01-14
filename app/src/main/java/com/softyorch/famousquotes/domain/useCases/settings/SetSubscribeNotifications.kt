package com.softyorch.famousquotes.domain.useCases.settings

import com.softyorch.famousquotes.domain.interfaces.IDatastore
import javax.inject.Inject

class SetSubscribeNotifications @Inject constructor(private val datastore: IDatastore) {
    suspend operator fun invoke() {
        datastore.setSubscribeNotifications()
    }
}

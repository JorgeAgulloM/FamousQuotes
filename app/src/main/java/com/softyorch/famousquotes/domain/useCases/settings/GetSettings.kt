package com.softyorch.famousquotes.domain.useCases.settings

import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.model.SettingsModel.Companion.toSettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSettings @Inject constructor(private val datastore: IDatastore) {
    operator fun invoke(): Flow<SettingsModel> =
        datastore.getSettings().map { it.toSettingsModel() }
}

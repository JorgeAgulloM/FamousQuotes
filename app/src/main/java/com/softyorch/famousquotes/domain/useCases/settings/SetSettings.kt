package com.softyorch.famousquotes.domain.useCases.settings

import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.model.SettingsModel
import com.softyorch.famousquotes.domain.model.SettingsModel.Companion.toSettingsStoreModel
import javax.inject.Inject

class SetSettings @Inject constructor(private val datastore: IDatastore) {
    suspend operator fun invoke(settings: SettingsModel) =
        datastore.setSettings(settings.toSettingsStoreModel())
}

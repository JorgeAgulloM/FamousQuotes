package com.softyorch.famousquotes.domain.interfaces

import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel
import kotlinx.coroutines.flow.Flow

interface IDatastore {
    fun getImageSet(): Flow<Set<String>>
    suspend fun setImageSet(images: Set<String>)
    fun getDbVersion(): Flow<String>
    suspend fun setDbVersion(dbVersion: String)
    fun getSettings(): Flow<SettingsStoreModel>
    suspend fun setSettings(settings: SettingsStoreModel)
    suspend fun setOnboarding(onboarding: Boolean)
}

package com.softyorch.famousquotes.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface IDatastore {
    fun getImageSet(): Flow<Set<String>>
    suspend fun setImageSet(images: Set<String>)
    fun getDbVersion(): Flow<String>
    suspend fun setDbVersion(dbVersion: String)
}

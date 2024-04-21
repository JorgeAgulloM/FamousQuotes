package com.softyorch.famousquotes.domain.interfaces

interface IConfigService {
    suspend fun getAppMinVersion(): String
    fun getUpdateDbVersion(): String
}

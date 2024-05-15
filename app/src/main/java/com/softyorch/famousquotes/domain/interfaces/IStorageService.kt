package com.softyorch.famousquotes.domain.interfaces

interface IStorageService {
    suspend fun getImage(url: String): String?
}

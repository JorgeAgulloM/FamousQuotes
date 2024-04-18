package com.softyorch.famousquotes.domain.interfaces

interface IStorageService {
    suspend fun getImages(): List<String>
}

package com.softyorch.famousquotes.domain.interfaces

interface IAuthService {
    suspend fun getAnonymousAuth(): Boolean
}
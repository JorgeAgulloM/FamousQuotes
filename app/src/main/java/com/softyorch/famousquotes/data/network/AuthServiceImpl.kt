package com.softyorch.famousquotes.data.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthServiceImpl @Inject constructor(private val auth: FirebaseAuth) : IAuthService {
    override suspend fun getAnonymousAuth(): Boolean =
        suspendCancellableCoroutine { cancelableCoroutine ->
            auth.signInAnonymously().addOnCompleteListener {
                cancelableCoroutine.resume(true)
            }.addOnFailureListener {
                cancelableCoroutine.resume(false)
            }
        }
}

interface IAuthService {
    suspend fun getAnonymousAuth(): Boolean
}

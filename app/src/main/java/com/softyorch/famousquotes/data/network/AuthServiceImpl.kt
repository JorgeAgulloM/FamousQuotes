package com.softyorch.famousquotes.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.softyorch.famousquotes.domain.interfaces.IAuthService
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthServiceImpl @Inject constructor(private val auth: FirebaseAuth) : IAuthService {
    override suspend fun getAnonymousAuth(): Boolean =
        suspendCancellableCoroutine { cancelableCoroutine ->
            try {
                auth.signInAnonymously().addOnSuccessListener {
                    cancelableCoroutine.resume(true)
                }.addOnFailureListener { ex ->
                    cancelableCoroutine.resumeWithException(ex)
                }
            } catch (fex: FirebaseAuthException) {
                cancelableCoroutine.resumeWithException(fex)
            } catch (ex: Exception) {
                cancelableCoroutine.resumeWithException(ex)
            }
        }
}

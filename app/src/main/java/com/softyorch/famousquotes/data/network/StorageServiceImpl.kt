package com.softyorch.famousquotes.data.network

import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
class StorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : IStorageService {

    companion object {
        private const val TIMEOUT: Long = 4000L
    }

    override suspend fun getImage(url: String): String? = try {
        withTimeoutOrNull(TIMEOUT) {
            storage.reference.child(url).downloadUrl.await().toString()
        }
    } catch (ex: Exception) {
        null
    }
}

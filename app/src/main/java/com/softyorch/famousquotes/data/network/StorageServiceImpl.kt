package com.softyorch.famousquotes.data.network

import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val storage: FirebaseStorage) :
    IStorageService {

    override suspend fun getImage(url: String): String =
        storage.reference.child(url).downloadUrl.await().toString()

}

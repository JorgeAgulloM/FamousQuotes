package com.softyorch.famousquotes.data.network

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.google.firebase.FirebaseException
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.core.APP_NAME
import com.softyorch.famousquotes.core.URL_STORAGE_PROJECT
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context,
) : IStorageService {

    companion object {
        private const val TIMEOUT: Long = 4000L
    }

    override suspend fun getImage(url: String): String? = try {
        withTimeoutOrNull(TIMEOUT) {
            try {
                storage.reference.child(url).downloadUrl.await().toString()
            } catch (ex: FirebaseException) {
                writeLog(ERROR, "Error from Firebase Storage: ${ex.cause}")
                null
            }
        }
    } catch (ex: Exception) {
        null
    }

    override suspend fun getImageList(): List<String>? {
        return withTimeoutOrNull(TIMEOUT) {
            try {
                val storageRef = storage.reference.child(URL_STORAGE_PROJECT)
                storageRef.listAll().await().prefixes.toList().map { it.name }
            } catch (ex: FirebaseException) {
                writeLog(ERROR, "Error from Firebase Storage: ${ex.cause}")
                null
            }
        }
    }

    override suspend fun downloadImage(imageId: String, result: (Boolean) -> Unit) {
        withTimeoutOrNull(30000) {
            try {
                val imageName = "$APP_NAME-$imageId.png"
                val url = "$URL_STORAGE_PROJECT$imageId/$imageId.png"
                val storageRef = storage.reference.child(url)

                storageRef.downloadUrl.addOnSuccessListener {
                    downloadFile(imageName, it.toString()) {
                        result(true)
                    }
                }.addOnFailureListener {
                    result(false)
                }

            } catch (fex: FirebaseException) {
                writeLog(ERROR, "Error downloading image. Error: ${fex.cause}")
                result(false)
            } catch (ex: Exception) {
                writeLog(ERROR, "Error creating image. Error: ${ex.cause}")
                result(false)
            }
        } ?: result(false)
    }

    private fun downloadFile(fileName: String, url: String?, result: () -> Unit) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri).apply {
            setTitle(fileName)
            setDescription(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        }

        downloadManager.enqueue(request).apply { result() }
    }
}

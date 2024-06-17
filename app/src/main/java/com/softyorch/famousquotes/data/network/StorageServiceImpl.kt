package com.softyorch.famousquotes.data.network

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.google.firebase.FirebaseException
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.core.APP_NAME
import com.softyorch.famousquotes.core.FIREBASE_TIMEOUT
import com.softyorch.famousquotes.core.URL_STORAGE_PROJECT
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.writeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context,
) : IStorageService {

    private val imageList: MutableList<String> = mutableListOf()

    override suspend fun getImage(url: String): String? = withTimeoutOrNull(FIREBASE_TIMEOUT) {
        getImageList()
        suspendCancellableCoroutine { cancelCoroutine ->
            try {
                val id = url.split("/").last().split(".").first()

                if (imageList.contains(id)) {
                    storage.reference.child(url).downloadUrl.addOnSuccessListener {
                        cancelCoroutine.resume(it.toString())
                    }.addOnFailureListener { ex ->
                        cancelCoroutine.resumeWithException(ex)
                    }
                } else {
                    cancelCoroutine.resume(null)
                }
            } catch (ex: FirebaseException) {
                writeLog(ERROR, "Error from Firebase: ${ex.cause}")
                cancelCoroutine.resumeWithException(ex)
            } catch (ex: Exception) {
                writeLog(ERROR, "Error Storage Service: ${ex.cause}")
                cancelCoroutine.resumeWithException(ex)
            }
        }
    }

    override suspend fun getImageList(): List<String>? {
        return withTimeoutOrNull(FIREBASE_TIMEOUT) {
            suspendCancellableCoroutine { cancelableCoroutine ->
                try {
                    if (imageList.size > 0) cancelableCoroutine.resume(imageList)

                    val ref = storage.reference.child(URL_STORAGE_PROJECT)
                    ref.listAll().addOnSuccessListener { result ->
                        if (result != null && result.items.size > 0) {
                            result.prefixes.toList().map {
                                it.name
                            }.apply {
                                imageList.addAll(this)
                            }
                            cancelableCoroutine.resume(imageList)
                        }
                    }.addOnFailureListener {
                        cancelableCoroutine.resume(imageList)
                    }

                } catch (ex: FirebaseException) {
                    writeLog(ERROR, "Error from Firebase Storage: ${ex.cause}")
                    cancelableCoroutine.resumeWithException(ex)
                } catch (ex: Exception) {
                    writeLog(ERROR, "Error from Storage: ${ex.cause}")
                    cancelableCoroutine.resumeWithException(ex)
                }
            }
        }
    }

    override suspend fun downloadImage(imageId: String, result: (Boolean) -> Unit) {
        withTimeoutOrNull(FIREBASE_TIMEOUT) {
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

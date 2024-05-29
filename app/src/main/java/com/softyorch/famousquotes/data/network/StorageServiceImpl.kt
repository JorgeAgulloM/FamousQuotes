package com.softyorch.famousquotes.data.network

import android.os.Environment
import com.google.firebase.FirebaseException
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.utils.LevelLog.ERROR
import com.softyorch.famousquotes.utils.LevelLog.INFO
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : IStorageService {

    companion object {
        private const val TIMEOUT: Long = 4000L
    }

    private val appName = BuildConfig.APP_TITLE

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

    override suspend fun downloadImage(imageId: String) {
        withTimeoutOrNull(5000) {
            try {
                val url = "/images/famous_quotes/historical_quotes/$imageId/$imageId.png"
                val urlDownload = withContext(Dispatchers.IO) { getImage(url) }

                if (!urlDownload.isNullOrBlank()) {
                    val storageRef = storage.reference.child(url)

                    val rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

                    val localFile = File(rootPath, "$appName-$imageId.png")

                    storageRef.getFile(localFile).addOnSuccessListener {
                        writeLog(INFO, "downloadImage -> DownloadImage is Success ImageId: $imageId")
                    }.addOnFailureListener {
                        writeLog(ERROR, "Error: downloadImage -> ImageId: $imageId. Error: ${it.cause}")
                    }
                }
            } catch (fex: FirebaseException) {
                writeLog(ERROR, "Error downloading image. Error: ${fex.cause}")
            } catch (ex: Exception) {
                writeLog(ERROR, "Error downloading image. Error: ${ex.cause}")
            }
        }
    }
}

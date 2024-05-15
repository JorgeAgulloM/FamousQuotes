package com.softyorch.famousquotes.data.network

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storageMetadata
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import com.softyorch.famousquotes.utils.LevelLog
import com.softyorch.famousquotes.utils.writeLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val storage: FirebaseStorage) :
    IStorageService {

    companion object {
        const val ROUTE_STORE = "/images/famous_quotes/${BuildConfig.DB_COLLECTION}/"
        const val ALTERNATIVE_ROUTE_STORE = "/temporal/famous_quotes/${BuildConfig.DB_COLLECTION}/"
    }

    override suspend fun getImages(): List<String> {
        val ref = storage.reference.child(ROUTE_STORE)
        val list = ref.listAll().await().items.map {
            val items = it.toString().split("/").size
            val route = storage.reference.child("${ROUTE_STORE}/${it.toString().split("/")[items -1]}").downloadUrl.await().toString()
            route
        }
        getTodayImage()
        Log.i("LOGTAG", "List: $list")
        return list
    }

    //Generar función para obtener la imagen de día.
    fun getTodayImage(todayId: String = "1703977200000") {
        writeLog(LevelLog.INFO, "ROUTE: $ROUTE_STORE")
        val completeRoute = "$ROUTE_STORE$todayId/"
        val ref = storage.reference.child(ROUTE_STORE)
        ref.child("$todayId.webp").downloadUrl.addOnFailureListener {
            Log.e("LOGTAG", "Exception Image of today = $it")
        }.addOnSuccessListener {
            Log.i("LOGTAG", "Image of today = $it")
        }

    }


    private suspend fun getRandomImage(): String {
        val ref = storage.reference.child(ROUTE_STORE)
        val list = ref.listAll().await()
        val imageUri = list.items.random()
        val image = imageUri.downloadUrl.await()
        return image.toString()
    }

    private fun seMetaData(): StorageMetadata = storageMetadata {
        contentType = "image/webp"
        setCustomMetadata("date", System.currentTimeMillis().toString())
        setCustomMetadata("owner", "SoftYorch")
        setCustomMetadata("createdBy", "With Midjourney by Jorge Agullo Martin")
    }
}
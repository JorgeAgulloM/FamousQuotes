package com.softyorch.famousquotes.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatastoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IDatastore {

    companion object {
        private val Context.datastore by preferencesDataStore(
            name = if (BuildConfig.DEBUG) "DailyElectricCostDev" else "DailyElectricCost"
        )

        private const val IMAGES_KEY = "ds_images"
    }

    override fun getImageSet(): Flow<Set<String>> = context.datastore.data.map { data ->
        data[stringSetPreferencesKey(IMAGES_KEY)] ?: emptySet()
    }

    override suspend fun setImageSet(images: Set<String>) {
        context.datastore.edit { data ->
            data[stringSetPreferencesKey(IMAGES_KEY)] = images
        }
    }
}

package com.softyorch.famousquotes.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel.Companion.AUTO_DARK_MODE
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel.Companion.DARK_MODE
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel.Companion.IS_SHOW_ON_BOARDING
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel.Companion.LEFT_HANDED
import com.softyorch.famousquotes.data.datastore.model.SettingsStoreModel.Companion.NOTIFICATION_CHANNEL
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatastoreImpl @Inject constructor(
    private val context: Context
) : IDatastore {

    companion object {
        private val Context.datastore by preferencesDataStore(
            name = if (BuildConfig.DEBUG) "${BuildConfig.APP_TITLE}_dev" else BuildConfig.APP_TITLE
        )

        private const val IMAGES_KEY = "ds_images"
        private const val DB_VERSION = "db_version"
    }


    /*********** Images ***************************************************************************/

    override fun getImageSet(): Flow<Set<String>> = context.datastore.data.map { data ->
        data[stringSetPreferencesKey(IMAGES_KEY)] ?: emptySet()
    }

    override suspend fun setImageSet(images: Set<String>) {
        context.datastore.edit { data ->
            data[stringSetPreferencesKey(IMAGES_KEY)] = images
        }
    }


    /*********** Db Version ***********************************************************************/

    override fun getDbVersion(): Flow<String> = context.datastore.data.map { data ->
        data[stringPreferencesKey(DB_VERSION)].orEmpty()
    }

    override suspend fun setDbVersion(dbVersion: String) {
        context.datastore.edit { data ->
            data[stringPreferencesKey(DB_VERSION)] = dbVersion
        }
    }


    /*********** Settings *************************************************************************/

    override fun getSettings(): Flow<SettingsStoreModel> =
        context.datastore.data.map { data ->
            SettingsStoreModel(
                autoDarkMode = data[booleanPreferencesKey(AUTO_DARK_MODE)] ?: true,
                darkMode = data[booleanPreferencesKey(DARK_MODE)] ?: false,
                leftHanded = data[booleanPreferencesKey(LEFT_HANDED)] ?: false,
                notificationChannel = data[booleanPreferencesKey(NOTIFICATION_CHANNEL)] ?: false,
                isShowOnBoarding = data[booleanPreferencesKey(IS_SHOW_ON_BOARDING)] ?: false
            )
        }

    override suspend fun setSettings(settings: SettingsStoreModel) {
        context.datastore.edit { data ->
            data[booleanPreferencesKey(AUTO_DARK_MODE)] = settings.autoDarkMode
            data[booleanPreferencesKey(DARK_MODE)] = settings.darkMode
            data[booleanPreferencesKey(LEFT_HANDED)] = settings.leftHanded
            data[booleanPreferencesKey(NOTIFICATION_CHANNEL)] = settings.notificationChannel
            data[booleanPreferencesKey(IS_SHOW_ON_BOARDING)] = settings.isShowOnBoarding
        }
    }

    override suspend fun setOnboarding(onboarding: Boolean) {
        context.datastore.edit { data ->
            data[booleanPreferencesKey(IS_SHOW_ON_BOARDING)] = onboarding
        }
    }

    override suspend fun setSubscribeNotifications() {
        context.datastore.edit { data ->
            data[booleanPreferencesKey(NOTIFICATION_CHANNEL)] = true
        }
    }
}

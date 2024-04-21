package com.softyorch.famousquotes.data.network

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IConfigService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConfigServiceImpl @Inject constructor(
    private val configService: FirebaseRemoteConfig,
) : IConfigService {

    companion object {
        const val APP_MIN_VERSION = "app_min_version"
        const val DB_VERSION = BuildConfig.DB_VERSION
    }

    override suspend fun getAppMinVersion(): String {
        configService.fetch(0)
        configService.activate().await()
        return getStringFromService(APP_MIN_VERSION)
    }

    override fun getUpdateDbVersion(): String = getStringFromService(DB_VERSION)

    private fun getStringFromService(string: String) = configService.getString(string)
}

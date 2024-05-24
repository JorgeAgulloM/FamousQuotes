package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IConfigService
import com.softyorch.famousquotes.domain.utils.emptyVersionList
import com.softyorch.famousquotes.domain.utils.versionList
import javax.inject.Inject

class TimeToUpdate @Inject constructor(private val configService: IConfigService) {

    suspend operator fun invoke(): Boolean {
        val minVersionAllowed = listVersionToInt(getMinVersion())
        val appVersion = listVersionToInt(getAppVersionCurrent())

        return minVersionAllowed > appVersion
    }

    private suspend fun getMinVersion(): List<Int> = try {
        val minVersion = configService.getAppMinVersion()
        if (minVersion.isBlank()) emptyVersionList() else versionList(minVersion)
    } catch (ex: Exception) {
        emptyVersionList()
    }

    private fun getAppVersionCurrent(): List<Int> = try {
        versionList(BuildConfig.VERSION_NAME)
    } catch (ex: Exception) {
        emptyVersionList()
    }

    private fun listVersionToInt(v: List<Int>): Int =
        v.joinToString(separator = "").toIntOrNull() ?: 0
}

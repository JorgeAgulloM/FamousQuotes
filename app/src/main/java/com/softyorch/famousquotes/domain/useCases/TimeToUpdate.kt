package com.softyorch.famousquotes.domain.useCases

import android.content.Context
import com.softyorch.famousquotes.domain.interfaces.IConfigService
import com.softyorch.famousquotes.domain.utils.emptyVersionList
import com.softyorch.famousquotes.domain.utils.versionList
import com.softyorch.famousquotes.ui.utils.extFunc.packageNameApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimeToUpdate @Inject constructor(
    private val configService: IConfigService,
    @ApplicationContext private val context: Context,
) {

    suspend operator fun invoke(): Boolean =
        !getAppVersionCurrent().zip(getMinVersion()).all { (appV, minV) -> appV >= minV }

    private fun getAppVersionCurrent(): List<Int> = try {
        val packageInfo = context.packageNameApp()
        versionList(packageInfo.versionName)
    } catch (ex: Exception) {
        emptyVersionList()
    }

    private suspend fun getMinVersion(): List<Int> = try {
        val minVersion = configService.getAppMinVersion()
        if (minVersion.isBlank()) emptyVersionList() else versionList(minVersion)
    } catch (ex: Exception) {
        emptyVersionList()
    }
}

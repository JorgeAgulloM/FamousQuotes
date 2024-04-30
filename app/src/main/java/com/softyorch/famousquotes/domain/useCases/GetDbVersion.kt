package com.softyorch.famousquotes.domain.useCases

import com.softyorch.famousquotes.domain.interfaces.IConfigService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.utils.emptyVersionList
import com.softyorch.famousquotes.domain.utils.versionList
import com.softyorch.famousquotes.domain.utils.versionToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetDbVersion @Inject constructor(
    private val configService: IConfigService,
    private val datastore: IDatastore,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(): Boolean {
        val dbCurrentVersion = withContext(dispatcherIO) { getDbVersionCurrent() }
        val dbUpdateVersion = getUpdateDbVersion()

        val needUpdate = !dbCurrentVersion.zip(dbUpdateVersion).all {
            (dbV, upV) -> dbV == upV
        }

        //writeLog(LevelLog.INFO, "[GetDbVersion] -> needUpdate: $needUpdate")

        if (needUpdate) setDbVersion(dbUpdateVersion)

        return needUpdate
    }

    private suspend fun getDbVersionCurrent(): List<Int> = try {
        val dbVersion = datastore.getDbVersion().first()
        versionList(dbVersion)
    } catch (ex: Exception) {
        emptyVersionList()
    }

    private fun getUpdateDbVersion(): List<Int> = try {
        val updateDbVersion = configService.getUpdateDbVersion()
        if (updateDbVersion.isBlank()) emptyVersionList() else versionList(updateDbVersion)
    } catch (ex: Exception) {
        emptyVersionList()
    }

    private suspend fun setDbVersion(dbUpdateVersion: List<Int>) {
        datastore.setDbVersion(versionToString(dbUpdateVersion))
    }

}

package com.softyorch.famousquotes.data.defaultDatabase

import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import javax.inject.Inject

class DefaultDatabaseImpl @Inject constructor() : IDefaultDatabase {

    companion object {
        const val HISTORICAL_FLAVOUR = "historical"
        const val BIBLICAL_FLAVOUR = "biblical"
        //const val UPLIFTING_FLAVOUR = "uplifting"
    }

    override fun getDefaultDatabase(): List<DefaultModel> =
        if (BuildConfig.APP_TITLE.contains(HISTORICAL_FLAVOUR))
            getDefaultHistoricalDatabase()
        else if (BuildConfig.APP_TITLE.contains(BIBLICAL_FLAVOUR))
            getDefaultBiblicalDatabase()
        else getDefaultUpliftingDatabase()

    private fun getDefaultHistoricalDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
        )
    }

    private fun getDefaultBiblicalDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
        )
    }

    private fun getDefaultUpliftingDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
            DefaultModel(
                owner = "",
                quote = listOf()
            ),
        )
    }

}
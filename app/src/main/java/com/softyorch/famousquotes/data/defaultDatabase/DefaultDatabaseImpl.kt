package com.softyorch.famousquotes.data.defaultDatabase

import android.content.Context
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultDatabaseImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IDefaultDatabase {

    companion object {
        const val HISTORICAL_FLAVOUR = "historical"
        const val BIBLICAL_FLAVOUR = "biblical"
        //const val UPLIFTING_FLAVOUR = "uplifting"
    }

    override fun getDefaultQuote(): DefaultModel =
        if (BuildConfig.APP_TITLE.contains(HISTORICAL_FLAVOUR))
            getDefaultHistoricalDatabase().random().copy(imageUrl = getImageFromMemory())
        else if (BuildConfig.APP_TITLE.contains(BIBLICAL_FLAVOUR))
            getDefaultBiblicalDatabase().random().copy(imageUrl = getImageFromMemory())
        else getDefaultUpliftingDatabase().random().copy(imageUrl = getImageFromMemory())

    override fun getDefaultImage(): String = getImageFromMemory()

    private fun getDefaultHistoricalDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "Albert Einstein",
                quote = listOf(context.getString(R.string.historical_quote_default_01))
            ),
            DefaultModel(
                owner = "Isaac Newton",
                quote = listOf(context.getString(R.string.historical_quote_default_02))
            ),
            DefaultModel(
                owner = "Mahatma Gandhi",
                quote = listOf(context.getString(R.string.historical_quote_default_03))
            ),
            DefaultModel(
                owner = "Marie Curie",
                quote = listOf(context.getString(R.string.historical_quote_default_04))
            ),
            DefaultModel(
                owner = "Leonardo da Vinci",
                quote = listOf(context.getString(R.string.historical_quote_default_05))
            ),
            DefaultModel(
                owner = "Winston Churchill",
                quote = listOf(context.getString(R.string.historical_quote_default_06))
            ),
            DefaultModel(
                owner = "Martin Luther King Jr.",
                quote = listOf(context.getString(R.string.historical_quote_default_07))
            ),
            DefaultModel(
                owner = "Mark Twain",
                quote = listOf(context.getString(R.string.historical_quote_default_08))
            ),
            DefaultModel(
                owner = "Frida Kahlo",
                quote = listOf(context.getString(R.string.historical_quote_default_09))
            ),
            DefaultModel(
                owner = "Steve Jobs",
                quote = listOf(context.getString(R.string.historical_quote_default_10))
            ),
        )
    }

    private fun getDefaultBiblicalDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "Salmo 23:1",
                quote = listOf(context.getString(R.string.biblical_quote_default_01))
            ),
            DefaultModel(
                owner = "Mateo 22:39",
                quote = listOf(context.getString(R.string.biblical_quote_default_02))
            ),
            DefaultModel(
                owner = "Proverbios 3:5",
                quote = listOf(context.getString(R.string.biblical_quote_default_03))
            ),
            DefaultModel(
                owner = "Juan 3:16",
                quote = listOf(context.getString(R.string.biblical_quote_default_04))
            ),
            DefaultModel(
                owner = "Juan 4:8",
                quote = listOf(context.getString(R.string.biblical_quote_default_05))
            ),
            DefaultModel(
                owner = "Isaías 41:10",
                quote = listOf(context.getString(R.string.biblical_quote_default_06))
            ),
            DefaultModel(
                owner = "Éxodo 20:12",
                quote = listOf(context.getString(R.string.biblical_quote_default_07))
            ),
            DefaultModel(
                owner = "Salmo 111:10",
                quote = listOf(context.getString(R.string.biblical_quote_default_08))
            ),
            DefaultModel(
                owner = "Salmo 27:1",
                quote = listOf(context.getString(R.string.biblical_quote_default_09))
            ),
            DefaultModel(
                owner = "Mateo 5:6",
                quote = listOf(context.getString(R.string.biblical_quote_default_10))
            ),
        )
    }

    private fun getDefaultUpliftingDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                owner = "Robert Collier",
                quote = listOf(context.getString(R.string.uplifting_quote_default_01))
            ),
            DefaultModel(
                owner = "Helen Keller",
                quote = listOf(context.getString(R.string.uplifting_quote_default_02))
            ),
            DefaultModel(
                owner = "Confucio",
                quote = listOf(context.getString(R.string.uplifting_quote_default_03))
            ),
            DefaultModel(
                owner = "George Eliot",
                quote = listOf(context.getString(R.string.uplifting_quote_default_04))
            ),
            DefaultModel(
                owner = "C.S. Lewis",
                quote = listOf(context.getString(R.string.uplifting_quote_default_05))
            ),
            DefaultModel(
                owner = "Theodore Roosevelt",
                quote = listOf(context.getString(R.string.uplifting_quote_default_06))
            ),
            DefaultModel(
                owner = "Albert Schweitzer",
                quote = listOf(context.getString(R.string.uplifting_quote_default_07))
            ),
            DefaultModel(
                owner = "Anónimo",
                quote = listOf(context.getString(R.string.uplifting_quote_default_08))
            ),
            DefaultModel(
                owner = "Tommy Lasorda",
                quote = listOf(context.getString(R.string.uplifting_quote_default_09))
            ),
            DefaultModel(
                owner = "Anónimo",
                quote = listOf(context.getString(R.string.uplifting_quote_default_10))
            ),
        )
    }

    private fun getImageFromMemory(): String {
        val listImages = listOf(
            "bg_image_01",
            "bg_image_02",
            "bg_image_03",
            "bg_image_04",
            "bg_image_05",
            "bg_image_06",
            "bg_image_07",
            "bg_image_08",
            "bg_image_09",
            "bg_image_10",
            "bg_image_11",
            "bg_image_12",
        )

        return listImages.random()
    }
}

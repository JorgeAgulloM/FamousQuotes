package com.softyorch.famousquotes.data.defaultDatabase

import android.content.Context
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import javax.inject.Inject

class DefaultDatabaseImpl @Inject constructor(
    private val context: Context
) : IDefaultDatabase {

    companion object {
        const val HISTORICAL_FLAVOUR = "historical"
        const val BIBLICAL_FLAVOUR = "biblical"
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
                id = "001",
                owner = "Albert Einstein",
                quote = listOf(context.getString(R.string.historical_quote_default_01))
            ),
            DefaultModel(
                id = "002",
                owner = "Isaac Newton",
                quote = listOf(context.getString(R.string.historical_quote_default_02))
            ),
            DefaultModel(
                id = "003",
                owner = "Mahatma Gandhi",
                quote = listOf(context.getString(R.string.historical_quote_default_03))
            ),
            DefaultModel(
                id = "004",
                owner = "Marie Curie",
                quote = listOf(context.getString(R.string.historical_quote_default_04))
            ),
            DefaultModel(
                id = "005",
                owner = "Leonardo da Vinci",
                quote = listOf(context.getString(R.string.historical_quote_default_05))
            ),
            DefaultModel(
                id = "006",
                owner = "Winston Churchill",
                quote = listOf(context.getString(R.string.historical_quote_default_06))
            ),
            DefaultModel(
                id = "007",
                owner = "Martin Luther King Jr.",
                quote = listOf(context.getString(R.string.historical_quote_default_07))
            ),
            DefaultModel(
                id = "008",
                owner = "Mark Twain",
                quote = listOf(context.getString(R.string.historical_quote_default_08))
            ),
            DefaultModel(
                id = "009",
                owner = "Frida Kahlo",
                quote = listOf(context.getString(R.string.historical_quote_default_09))
            ),
            DefaultModel(
                id = "010",
                owner = "Steve Jobs",
                quote = listOf(context.getString(R.string.historical_quote_default_10))
            ),
        )
    }

    private fun getDefaultBiblicalDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                id = "001",
                owner = "Salmo 23:1",
                quote = listOf(context.getString(R.string.biblical_quote_default_01))
            ),
            DefaultModel(
                id = "002",
                owner = "Mateo 22:39",
                quote = listOf(context.getString(R.string.biblical_quote_default_02))
            ),
            DefaultModel(
                id = "003",
                owner = "Proverbios 3:5",
                quote = listOf(context.getString(R.string.biblical_quote_default_03))
            ),
            DefaultModel(
                id = "004",
                owner = "Juan 3:16",
                quote = listOf(context.getString(R.string.biblical_quote_default_04))
            ),
            DefaultModel(
                id = "005",
                owner = "Juan 4:8",
                quote = listOf(context.getString(R.string.biblical_quote_default_05))
            ),
            DefaultModel(
                id = "006",
                owner = "Isaías 41:10",
                quote = listOf(context.getString(R.string.biblical_quote_default_06))
            ),
            DefaultModel(
                id = "007",
                owner = "Éxodo 20:12",
                quote = listOf(context.getString(R.string.biblical_quote_default_07))
            ),
            DefaultModel(
                id = "008",
                owner = "Salmo 111:10",
                quote = listOf(context.getString(R.string.biblical_quote_default_08))
            ),
            DefaultModel(
                id = "009",
                owner = "Salmo 27:1",
                quote = listOf(context.getString(R.string.biblical_quote_default_09))
            ),
            DefaultModel(
                id = "010",
                owner = "Mateo 5:6",
                quote = listOf(context.getString(R.string.biblical_quote_default_10))
            ),
        )
    }

    private fun getDefaultUpliftingDatabase(): List<DefaultModel> {
        return listOf(
            DefaultModel(
                id = "001",
                owner = "Robert Collier",
                quote = listOf(context.getString(R.string.uplifting_quote_default_01))
            ),
            DefaultModel(
                id = "002",
                owner = "Helen Keller",
                quote = listOf(context.getString(R.string.uplifting_quote_default_02))
            ),
            DefaultModel(
                id = "003",
                owner = "Confucio",
                quote = listOf(context.getString(R.string.uplifting_quote_default_03))
            ),
            DefaultModel(
                id = "004",
                owner = "George Eliot",
                quote = listOf(context.getString(R.string.uplifting_quote_default_04))
            ),
            DefaultModel(
                id = "005",
                owner = "C.S. Lewis",
                quote = listOf(context.getString(R.string.uplifting_quote_default_05))
            ),
            DefaultModel(
                id = "006",
                owner = "Theodore Roosevelt",
                quote = listOf(context.getString(R.string.uplifting_quote_default_06))
            ),
            DefaultModel(
                id = "007",
                owner = "Albert Schweitzer",
                quote = listOf(context.getString(R.string.uplifting_quote_default_07))
            ),
            DefaultModel(
                id = "008",
                owner = "Anónimo",
                quote = listOf(context.getString(R.string.uplifting_quote_default_08))
            ),
            DefaultModel(
                id = "009",
                owner = "Tommy Lasorda",
                quote = listOf(context.getString(R.string.uplifting_quote_default_09))
            ),
            DefaultModel(
                id = "010",
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

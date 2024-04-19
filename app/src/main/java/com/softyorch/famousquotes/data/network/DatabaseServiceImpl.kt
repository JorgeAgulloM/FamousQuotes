package com.softyorch.famousquotes.data.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.softyorch.famousquotes.BuildConfig
import com.softyorch.famousquotes.data.network.response.QuoteResponse
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
): IDatabaseService {

    companion object {
        const val COLLECTION = BuildConfig.DB_COLLECTION
    }

    override suspend fun getQuote(id: String): QuoteResponse? {
//        getAllQuotes()

        val document = firestore.collection("${COLLECTION}3").document(id)

        return try {
            document.get(Source.CACHE).await().toObject(QuoteResponse::class.java)
        } catch (ex: Exception) {
            document.get().await().toObject(QuoteResponse::class.java)
        }
    }


    private suspend fun getAllQuotes() {
        val document = firestore.collection("${COLLECTION}3").orderBy("id").get().await().map {
            it.toObject(QuoteResponse::class.java)
        }
        setUpdate(document)
    }

    private suspend fun setUpdate(quotes: List<QuoteResponse>) {

        val images = listOf(
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_1.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_2.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_3.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_4.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_5.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_6.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_7.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_8.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_9.webp",
            "gs://famousquotes-79dd0.appspot.com/temporal/famous_quotes/historical_quotes/bg_image_10.webp"
        )

        var day = 1
        quotes.forEach { quote ->
            val id = getIdFromCalendarStartZero(day)
            val date = getToday(id.toLong())

            day += 1
            val newData = hashMapOf(
                "id" to id,
                "owner" to quote.owner,
                "quote" to listOf(quote.quote[0]),
                "imageUrl" to images.random(),
                "date" to date
            )

            firestore.collection("${COLLECTION}3").document(id).set(newData).await()
        }
    }

    private fun getToday(timeInMillis: Long) = Timestamp((timeInMillis / 1000), 0)

    private fun getIdFromCalendarStartZero(day: Int): String {        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.HOUR_OF_DAY, day)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val oneHourInMillis = 1000 * 60 * 60
        return (calendar.timeInMillis - oneHourInMillis).toString()
    }

}

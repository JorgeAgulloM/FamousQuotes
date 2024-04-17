package com.softyorch.famousquotes.domain

import com.softyorch.famousquotes.domain.model.FamousQuoteModel
import com.softyorch.famousquotes.domain.model.FamousQuoteModel.Companion.toDomain
import java.util.Calendar
import javax.inject.Inject

class SelectRandomQuote @Inject constructor(private val db: IDatabaseService) {
    suspend operator fun invoke(): FamousQuoteModel? {
        val id = getTodayId()
        return db.getQuote(id)?.toDomain()
    }

    private fun getTodayId(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis.toString()
    }

}

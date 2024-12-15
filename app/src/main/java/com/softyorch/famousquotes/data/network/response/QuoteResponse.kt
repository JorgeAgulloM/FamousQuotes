package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class QuoteResponse(
    val id: String = "",
    val owner: List<String> = emptyList(),
    val quote: List<String> = emptyList(),
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val likes: Int = 0,
    val showns: Int = 0,
    val favorites: Int = 0
)

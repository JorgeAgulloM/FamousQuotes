package com.softyorch.famousquotes.data.network.response

import com.google.firebase.Timestamp

data class QuoteResponse(
    val id: String = "",
    val owner: String = "",
    val quote: List<String> = emptyList(),
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
)

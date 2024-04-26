package com.softyorch.famousquotes.data.network.response

import com.google.firebase.Timestamp

data class QuoteResponse(
    val id: String = "",
    val owner: List<String> = emptyList(),
    val quote: List<String> = emptyList(),
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
)

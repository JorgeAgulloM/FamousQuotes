package com.softyorch.famousquotes.data.network.response

import com.google.firebase.Timestamp

data class QuoteResponse(
    val id: String,
    val owner: String,
    val bodyList: List<String>,
    val date: Timestamp,
    val imageUrl: String,
)

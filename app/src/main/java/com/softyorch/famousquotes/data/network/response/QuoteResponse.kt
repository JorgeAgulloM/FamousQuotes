package com.softyorch.famousquotes.data.network.response

import androidx.annotation.Keep

@Keep
data class QuoteResponse(
    val id: String = "",
    val owner: List<String> = emptyList(),
    val quote: List<String> = emptyList(),
    val imageUrl: String = ""
)

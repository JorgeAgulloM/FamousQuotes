package com.softyorch.famousquotes.data.defaultDatabase

data class DefaultModel(
    val owner: String = "",
    val quote: List<String> = emptyList(),
    val imageUrl: String = "",
)

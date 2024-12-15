package com.softyorch.famousquotes.data.defaultDatabase

data class DefaultModel(
    val id: String = "0000000000000001",
    val owner: String = "",
    val quote: List<String> = emptyList(),
    val imageUrl: String = "",
    val likes: Int = 0,
    val shown: Int = 0,
    val favorites: Int = 0
)

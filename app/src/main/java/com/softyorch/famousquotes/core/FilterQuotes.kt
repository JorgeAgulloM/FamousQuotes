package com.softyorch.famousquotes.core

sealed interface FilterQuotes {
    data object Likes : FilterQuotes
    data object Favorites : FilterQuotes
    data object Shown : FilterQuotes
}

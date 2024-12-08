package com.softyorch.famousquotes.data.network.databaseService

import com.softyorch.famousquotes.BuildConfig

const val COLLECTION = BuildConfig.DB_COLLECTION
const val COLLECTION_USERS = "${COLLECTION}_users"
const val SERVICE_NAME = "FireStore ->"
const val LIKES = "likes"
const val LIKE_USERS = "likeUsers"
const val LIKE_QUOTES = "likeQuotes"
const val SHOWN = "shown"
const val SHOWN_USERS = "shownUsers"
const val SHOWN_QUOTES = "shownQuotes"
const val FAVORITES = "favorites"
const val FAVORITE_USERS = "favoriteUsers"
const val FAVORITE_QUOTES = "favoriteQuotes"
package com.softyorch.famousquotes.ui.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Home

@Serializable
object Grid

@Serializable
object Settings

@Serializable
data class Detail(val id: String)

@Serializable
object OnBoarding

@Serializable
object Info

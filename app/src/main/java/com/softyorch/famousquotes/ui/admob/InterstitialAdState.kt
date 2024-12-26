/*
 * Copyright (c) 2023. File developed for DailyElectricCost App by Jorge Agulló Martín for SoftYorch
 */

package com.softyorch.famousquotes.ui.admob

sealed class InterstitialAdState {
    data object Loading : InterstitialAdState()
    data object Showed : InterstitialAdState()
    data object Impression : InterstitialAdState()
    data object Clicked : InterstitialAdState()
    data object Close : InterstitialAdState()
    data class Error(val message: String) : InterstitialAdState()
}
